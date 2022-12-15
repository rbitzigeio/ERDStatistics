/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author cyqjefe0019
 */
public class Controller implements Initializable {
    
    @FXML private AnchorPane anchorPane;
    @FXML private TextField  tfDate;
    @FXML private TextField  tfTitle;
    @FXML private TreeView   tvStatistic;
    @FXML private TextField  tfLog;
    @FXML private Pane       bpCenterChart;
    @FXML private Pane       bpCenterSelection;
    @FXML private ComboBox   cbFrequence;
    @FXML private Menu       mData;
    @FXML private Button     bLinearReg;
    @FXML private Button     bMaxPerHour;
    @FXML private TextArea   taLogger;
    @FXML private Label      lBandWidth;
    @FXML private Label      lDate;
    @FXML private Label      lModel;
    @FXML private Label      lTotal;
    @FXML private TabPane    tpRoot;
    
    private int              _frequence = 1;
    private int              _unit = 0; // Default unit Bits/s, 1 = %
    private double           _WindowX;
    private double           _WindowY;
    private boolean          _bUpdateLastLineChart = false;   
    private File             _lastFile;    
    private Model            _model; 
    private boolean          bUpdateLastLineChart = false;
    private File             lastFile;
    
    private LineChart<Number,Number>    lastLineChart;
    private LineChart<Number,Number>    linRegLineChart;
    private Stage                       lastStage;
    private Stage                       _parentWindow;
    private Stage                       _lastStage;
    private ArrayList<Stage>            _alStages = new ArrayList<>();
    private TreeItem<String>            _selectedItem;
    private LineChart<Number,Number>    _lastLineChart;
    private LineChart<Number,Number>    _selectedLineChart;

    private static Logger               _LOGGER;

    //------------------------------------
    // Exit application and close windows
    @FXML private void handleExitAction(ActionEvent event) {
        log("Controller: handleExitAction");
        System.exit(0);
    }
    //-------------------
    // Linear Regression
    @FXML private void handleLinRegAction(ActionEvent event) {
        log("Controller: handleLinRegAction");
        if (linRegLineChart != null) {
            log("Calculate linear regression for chart : " + linRegLineChart.getTitle());
            int seriesSize = linRegLineChart.getData().get(0).getData().size();
            double[] aXin  = new double[seriesSize];
            double[] aYin  = new double[seriesSize];
            double[] aXout = new double[seriesSize];
            double[] aYout = new double[seriesSize]; // [olSeriesIn.size()];
            //( Get data of Chart
            getDataOfSeries(linRegLineChart, aXin, aYin, aXout, aYout);
            // Calculate linear regression 
            LinearRegression lrIn  = new LinearRegression(aXin, aYin);
            LinearRegression lrOut = new LinearRegression(aXout, aYout);
            Series seriesLRIn  = new Series();
            Series seriesLROut = new Series();
            // Only first and last point neccessary for definition of line
            // Y = intercept + x * slope // y = y(0) + x * Steigung
            // Total sum of Bandwidth
            // Half of sum of 1st value and last value
            // 6`*60*24 = seconds per day
            // Divided by 8 = bytes
            // Divided by 1000 * 1000 = TerraBytes 
            double dInTotal  = (lrIn.intercept()  + lrIn.intercept()  + seriesSize * _frequence*lrIn.slope() ) / 2.0 * (60*60*24) / 8 / 1000 / 1000;
            double dOutTotal = (lrOut.intercept() + lrOut.intercept() + seriesSize * _frequence*lrOut.slope()) / 2.0 * (60*60*24) / 8 / 1000 / 1000;
            lTotal.setText(String.format( "%.3f", dInTotal )  + " / " + String.format( "%.3f", dOutTotal) + " TByte");
            seriesLRIn.getData().add(new XYChart.Data(0,lrIn.intercept())); 
            seriesLRIn.getData().add(new XYChart.Data(seriesSize * _frequence,lrIn.intercept() + seriesSize * _frequence*lrIn.slope()));    
            seriesLROut.getData().add(new XYChart.Data(0, lrOut.intercept()));
            seriesLROut.getData().add(new XYChart.Data(seriesSize * _frequence, lrOut.intercept() + seriesSize * _frequence*lrOut.slope()));
            seriesLRIn.setName("LR In-Bound");
            seriesLROut.setName("LR Out-Bound");
            linRegLineChart.getData().addAll(seriesLRIn, seriesLROut);
            String lrTitle = linRegLineChart.getTitle() + " LR";
            linRegLineChart.setTitle(lrTitle);
            bLinearReg.setDisable(true);
        }
    }
    //--------------------------------------------
    // Create chart for maximal bandwith per hour
    @FXML private void handleMaxPerHourAction(ActionEvent event) {
        log("Controller: handleMaxPerHourAction");
         if (linRegLineChart != null) {
            log("Calculate linear regression for chart : " + linRegLineChart.getTitle());
            int seriesSize =linRegLineChart.getData().get(0).getData().size();
            double[] aXin  = new double[seriesSize];
            double[] aYin  = new double[seriesSize];
            double[] aXout = new double[seriesSize];
            double[] aYout = new double[seriesSize]; // [olSeriesIn.size()];
            //( Get data of Chart
            getDataOfSeries(linRegLineChart, aXin, aYin, aXout, aYout);
            getMaxPerHourDataOfSeries(aXin, aYin, aXout, aYout);
            // Build series in and out bound for chart
            Series seriesLRIn  = new Series();
            Series seriesLROut = new Series();
            // Calculate max values per hour
            int startX;
            int endX;
            int maxInY  = 0;
            int maxOutY = 0;
            for (int i=0; i<seriesSize; i++) {
                if (aYin[i] > maxInY) {
                    maxInY = (int)aYin[i];
                }
                if (aYout[i] > maxOutY) {
                    maxOutY = (int)aYout[i];
                }
                if (i > 0 && i % 60 == 0) {
                    startX = i - 60;
                    endX   = i;
                    seriesLRIn.getData().add(new XYChart.Data(startX,maxInY)); 
                    seriesLRIn.getData().add(new XYChart.Data(endX,maxInY)); 
                    seriesLROut.getData().add(new XYChart.Data(startX,maxOutY)); 
                    seriesLROut.getData().add(new XYChart.Data(endX,maxOutY));
                    maxInY  = 0;
                    maxOutY = 0;
                }
            }
            // Insert last points of series into chart
            seriesLRIn.getData().add(new XYChart.Data(seriesSize-60,maxInY)); 
            seriesLRIn.getData().add(new XYChart.Data(seriesSize,maxInY)); 
            seriesLROut.getData().add(new XYChart.Data(seriesSize-60,maxOutY)); 
            seriesLROut.getData().add(new XYChart.Data(seriesSize,maxOutY));
            // max value per hour
            seriesLRIn.setName("Max In-Bound");
            seriesLROut.setName("Max Out-Bound");
            linRegLineChart.getData().addAll(seriesLRIn, seriesLROut);
            String lrTitle = linRegLineChart.getTitle() + " Max/h";
            linRegLineChart.setTitle(lrTitle);
            bMaxPerHour.setDisable(true);
        }
    }
    //-------------------
    // Load initial data
    public void loadData() {
        tfLog.setText("Load data");
        log("Controller: loadData");
        loadProjectTree();
        loadModelMenues();
        bLinearReg.setDisable(true);
        bMaxPerHour.setDisable(true);
        lModel.setText(_model.getName());
    }
    //--------------------------------------------------------------------------------------
    // Methode wird nach Änderung des Verzeichnisses mit Statistikdaten mehrmals aufgerufen.
    // Grund unbekannt!!!
    private void updateChart() {
        log("Controller: updateChart");
        if (_selectedItem != null) {
            bLinearReg.setDisable(false);
            bMaxPerHour.setDisable(false);
            lDate.setText("");
            lBandWidth.setText("");
            lTotal.setText("");
            TreeItem<String> selectedDDItem   = (TreeItem<String>) _selectedItem;
            TreeItem<String> selectedMMItem   = (TreeItem<String>) selectedDDItem.getParent();
            TreeItem<String> selectedYYYYItem = (TreeItem<String>) selectedMMItem.getParent();

            String fileName = selectedYYYYItem.getValue() + "." + selectedMMItem.getValue() + "." + selectedDDItem.getValue();
            File f = _model.getCsvDataFiles(fileName); 
            lModel.setText(_model.getName());
            if (f != null) {
                tfLog.setText(f.getName());
                log("Selected file : " + f.getName());
                final NumberAxis                 xAxis         = new NumberAxis();   
                final NumberAxis                 yAxis         = new NumberAxis();   
                final LineChart<Number,Number>   lineChart     = new LineChart<>(xAxis,yAxis); // LineChart in main window
                final NumberAxis                 xActAxis      = new NumberAxis();   
                final NumberAxis                 yActAxis      = new NumberAxis();  
                final LineChart<Number,Number>   actLineChart  = new LineChart<>(xActAxis,yActAxis); // Small LineChart of actual selection
                final NumberAxis                 xDiffAxis     = new NumberAxis();   
                final NumberAxis                 yDiffAxis     = new NumberAxis();  
                final LineChart<Number,Number>   diffLineChart = new LineChart<>(xDiffAxis,yDiffAxis); // Diff LIneChart between actual and last chart
                // Create data for chart
                Series[] series = createChart(f);

                lineChart.getData().addAll(series[0], series[1]);
                linRegLineChart = lineChart;
                setLayoutLineChart(lineChart, lineChart.getTitle(), false, bpCenterChart.getWidth(), bpCenterChart.getHeight(), 0.0, 0.0);
                // Check Unit fron data file  
                String sUnit;
                if (_unit == 0) {
                    sUnit = " (in MBit/s)"; 
                } else {
                    sUnit = " (in %)"; 
                }
                // Decribe Chart: Date and Unit
                lineChart.setTitle("Bandwidth : " + selectedDDItem.getValue() + "." + selectedMMItem.getValue() + "." + selectedYYYYItem.getValue() + sUnit);
                bpCenterChart.getChildren().clear();
                bpCenterChart.getChildren().add(lineChart);
                lineChartsActions(lineChart, xAxis, yAxis);
                // show act, last and diff
                if (_bUpdateLastLineChart && _lastLineChart != null) {
                    
                    Stage stage     = new Stage();
                    Group group     = new Group();
                    Scene scene     = new Scene(group, 320,anchorPane.getHeight());
                    scene.getStylesheets().add("de/dpdhl/pup/ta/erd/Stylesheet.css");
                    stage.setX(_parentWindow.getX() + anchorPane.getWidth());
                    stage.setY(_parentWindow.getY());
                    
                    Pane  pane      = new Pane();
                    Pane  diffPane  = new Pane();
                    Pane  lastPane  = new Pane();
                    
                    pane.setPrefSize(300.,200.);
                    pane.setLayoutX(0);
                    pane.setLayoutY(0);

                    diffPane.setPrefSize(300.,200.);
                    diffPane.setLayoutX(0);
                    diffPane.setLayoutY(200);

                    lastPane.setPrefSize(300.,200.);
                    lastPane.setLayoutX(0);
                    lastPane.setLayoutY(400);
                    
                    calculateLineCharts(lineChart, actLineChart, diffLineChart, _lastLineChart);
                    setLayoutLineChart(actLineChart, lineChart.getTitle(), false, 300, 200, 0.0, 0.0);
                    setLayoutLineChart(diffLineChart, "Difference", false, 300, 200, 0.0, 0.0);
                    setLayoutLineChart(_lastLineChart, _lastLineChart.getTitle(),false, 300, 200, 0.0, 0.0);

                    if (!lineChart.getTitle().equals(_lastLineChart.getTitle())) {
                        //pane.getChildren().add(actLineChart);
                        //diffPane.getChildren().add(diffLineChart);
                        //lastPane.getChildren().add(_lastLineChart);
                        
                        pane.getChildren().clear();
                        pane.getChildren().add(actLineChart);
                        diffPane.getChildren().clear();
                        diffPane.getChildren().add(diffLineChart);
                        lastPane.getChildren().clear();
                        lastPane.getChildren().add(_lastLineChart);
                        //pLastChart.getChildren().addAll(actLineChart, diffLineChart, _lastLineChart);
                        group.getChildren().addAll(pane, diffPane, lastPane);
                        //group.getChildren().addAll(pActChart, pDiffChart, pLastChart);

                        stage.setScene(scene);
                        stage.show();
                        _lastStage = stage;     
                        _alStages.add(stage);
                    }
                } else {
                    _bUpdateLastLineChart = true;
                }
                _lastLineChart = lineChart; 
            } 
        }
    }
    //----------------------------------------------
    // Create chart for in bound and out bound data
    private Series[] createChart(File f) {
        log("Controller: createChart");
        Series        seriesIn  = new Series();
        Series        seriesOut = new Series();
        Series[]      series    = {seriesIn, seriesOut};
        List<XYChart.Data> colIn  = new ArrayList();
        List<XYChart.Data> colOut = new ArrayList();
        String  inString  = "";
        boolean bStart    = false;
        int     i         = 0;
        int     iSum      = 0;
        _unit = 0; // Default Unit 
        seriesIn.setName("In-Bound");
        seriesOut.setName("Out-Bound");
        try (FileReader inFile = new FileReader(f);                  
            BufferedReader inStream = new BufferedReader(inFile)) {   
            long sumValueIn  = 0;
            long sumValueOut = 0;
            boolean bBreak   = false;
            while ((inString = inStream.readLine()) != null) { // Read data until line with unix time is passed
                if (bStart) {
                    String[] s = inString.split(",");
                    if (s.length > 3) {
                        i++;
                        String[] s0  = s[3].split("\\.");
                        String[] s1  = s[4].split("\\.");
                        String day = s[1].substring(1) + s[2].substring(0,11);
                        // get formatted bandwidth
                        long valueIn  = parseBandwith(i, _unit, s0[0]);
                        long valueOut = parseBandwith(i, _unit, s1[0]);
                        sumValueIn   = sumValueIn  + valueIn; 
                        sumValueOut  = sumValueOut + valueOut;
                        iSum         = iSum + i;
                        if (i % _frequence == 0) {
                            XYChart.Data xyIn  = new XYChart.Data(iSum/_frequence, sumValueIn/_frequence);
                            XYChart.Data xyOut = new XYChart.Data(iSum/_frequence, sumValueOut/_frequence);
                            colIn.add(xyIn); // Average
                            colOut.add(xyOut);
                            sumValueIn  = 0;
                            sumValueOut = 0;
                            iSum        = 0;
                        } 
                    } // First empty line after data defines end of statistic section
                    if (inString.length() == 0) {
                        log(i + " Lines in data file  " + f.getName());
                        bBreak = true;
                    }
                } else {
                    if (inString.startsWith("[Line chart: Traffic Volume by Avg % Util (In)")) { 
                        _unit = 1; // % Unit or Bit/s (0 = default)
                    }
                    if (inString.startsWith("\"unix time\"")) {
                        bStart = true;
                    }
                }
                if (bBreak) {
                    break; // WHILE -> Stopp reading datafile
                }
            } 
        } catch (Exception ex) {  
            log("Line : " + i + " " + inString);
            log("Error reading file: " + ex.getMessage()) ;
        } 
        ObservableList<XYChart.Data> olIn  = FXCollections.observableArrayList(colIn);
        ObservableList<XYChart.Data> olOut = FXCollections.observableArrayList(colOut);
        seriesIn.getData().addAll(olIn);
        seriesOut.getData().addAll(olOut);
        return series;    
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log("Controller: initialize");
    }       

    private String setTVItem(String s, String lasts, TreeItem<String> item, TreeItem<String> parentItem) {
        log("Controller: setTVItem");
        if (!s.equals(lasts)) {
            item.setValue(s);
            parentItem.getChildren().add(item);
            lasts = s;
        }
        return lasts;
    }
    //-------------------------------------
    // Action to change value of frequence
    @FXML private void handleFrequenceAction(ActionEvent event) {
        log("Controller: handleFrequenceAction");
        ComboBox cb = (ComboBox)event.getSource();
        String frequence = cb.getSelectionModel().getSelectedItem().toString();
        String[] f = frequence.split("min");
        _frequence = Integer.parseInt(f[0]);
        clear();
        updateChart();
        log("Frequence changed to : " + _frequence);
    }
    //--------------------------------------------------
    // Set window coordinates of main window
    // Data is used to define position of child windows
    public void setWindow(double x, double y) {
        log("Controller: setWindow");
        _WindowX = x;
        _WindowY = y;
    }
    //----------------------------------------
    // Stage of parent window defined in main
    public void setParentWindow(Stage stage) {
        log("Controller: setParentWindow");
        _parentWindow = stage;
    }
    //----------------------------------------------------------------------------
    // Data of actual line chart and last line chart is used to compare this data
    // and visualize data and diff of data in seperated window
    private void calculateLineCharts(LineChart<Number, Number> lineChart, 
                                     LineChart<Number, Number> actLineChart, 
                                     LineChart<Number, Number> diffLineChart, 
                                     LineChart<Number, Number> _lastLineChart) {
        log("Controller: calculateLineCharts");
        Series seriesActIn   = new Series();
        Series seriesActOut  = new Series();
        Series seriesDiffIn  = new Series();
        Series seriesDiffOut = new Series();
        int diffX; 
        int diffYin; 
        int diffYout;
        boolean bUpdate = false;
        // actual linechart
        ObservableList<Series<Number, Number>> olLineChart = lineChart.getData();
        Series seriesIn  = olLineChart.get(0);
        Series seriesOut = olLineChart.get(1);
        ObservableList olSeriesIn  = seriesIn.getData();
        ObservableList olSeriesOut = seriesOut.getData();
        // last linechart
        ObservableList<Series<Number, Number>> olLastLineChart = _lastLineChart.getData();
        Series seriesLastIn  = olLastLineChart.get(0);
        Series seriesLastOut = olLastLineChart.get(1);
        ObservableList olLastSeriesIn  = seriesLastIn.getData();
        ObservableList olLastSeriesOut = seriesLastOut.getData();
        // Definition of lines/points in chart
        if (olSeriesIn.size() == olLastSeriesIn.size() && olSeriesOut.size() == olLastSeriesOut.size()) {
            for (int i=0; i<olSeriesIn.size(); i++) {
                // actual data
                Data data = (Data)olSeriesIn.get(i);
                Integer xIn = Integer.valueOf(data.getXValue().toString());
                Integer yIn = Integer.valueOf(data.getYValue().toString());
                data = (Data)olSeriesOut.get(i);
                Integer xOut = Integer.valueOf(data.getXValue().toString());
                Integer yOut = Integer.valueOf(data.getYValue().toString());
                // last data
                Data lastData = (Data)olLastSeriesIn.get(i);
                //Integer lastXIn = Integer.valueOf(lastData.getXValue().toString());
                Integer lastYIn = Integer.valueOf(lastData.getYValue().toString());
                lastData = (Data)olLastSeriesOut.get(i);
                //Integer lastXOut = Integer.valueOf(lastData.getXValue().toString());
                Integer lastYOut = Integer.valueOf(lastData.getYValue().toString());
                // diff data
                diffX    = xIn;
                diffYin  = yIn - lastYIn;
                diffYout = yOut - lastYOut;
                seriesDiffIn.getData().add(new XYChart.Data(diffX, diffYin));
                seriesDiffOut.getData().add(new XYChart.Data(diffX, diffYout));
                seriesActIn.getData().add(new XYChart.Data(xIn, yIn));
                seriesActOut.getData().add(new XYChart.Data(xOut, yOut));
                bUpdate = true;
            }
        }
        if (bUpdate) {
            diffLineChart.getData().addAll(seriesDiffIn, seriesDiffOut);
            actLineChart.getData().addAll(seriesActIn, seriesActOut);
        }
    }
    //---------------------------------
    // Definition of line chart layout 
    private void setLayoutLineChart(LineChart lc, String title, boolean b, double width, double height, double x, double y) {
        lc.setTitle(title);
        lc.setCreateSymbols(b);
        lc.setPrefSize(width, height);
        lc.setLayoutX(x);
        lc.setLayoutY(y);    
    }
    //---------------------------------
    // Load menue of project into menu
    private void loadModelMenues() {
        log("Controller: loadModelMenues");
        HashMap<String, String> hm = _model.getCsvDirs();
        String firstModel = "";
        for (String key : hm.keySet()) {
            String v = hm.get(key);
            if (firstModel.length()==0) {
                firstModel = v;
            }
            MenuItem mi = new MenuItem(v);
            log("Model inserted " + v);
            mData.getItems().add(mi);
            mi.addEventHandler(EventType.ROOT, (Event event) -> {
                MenuItem mii = (MenuItem)event.getSource();
                _model.setName(mii.getText());
                _model.setCsvDir(mii.getText());
                log("Model changed to " + mii.getText());
                bpCenterChart.getChildren().clear();
                clear();
                loadProjectTree();
            });
        }
        _model.setName(firstModel);
        lModel.setText(firstModel);
    }
    //-----------------------------------------
    // Load tree for selected platform/project
    private void loadProjectTree() {
        log("Controller: loadProjectTree");
        _lastLineChart = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        tfDate.setText(formatter.format(date));
        TreeItem<String> rootItem = new TreeItem<>("Express Route Direct");
        rootItem.setExpanded(true);
        tvStatistic.setRoot(rootItem);
        try {
            if (_model == null) {
                _model = new Model();
            } 
        } catch (IOException ex) {
            log("Missing model");
        }
        ContextMenu datePeriod = new ContextMenu();
        MenuItem startItem = new MenuItem("set Startdate");
        MenuItem stopItem = new MenuItem("set Enddate");
        startItem.setOnAction(new EventHandler(){
            @Override
            public void handle(Event e) {
                TreeItem tiMonth = _selectedItem.getParent();
                if (tiMonth != null) {
                    TreeItem tiYear = tiMonth.getParent();
                    String startDate = tiYear.getValue().toString() + "." + tiMonth.getValue().toString() + "." + _selectedItem.getValue();
                    startItem.setText(startDate);
                    log("Start date : " + startDate + " selected");
                }
            }
        });
        stopItem.setOnAction(new EventHandler(){
            @Override
            public void handle(Event e) {
                TreeItem tiMonth = _selectedItem.getParent();
                if (tiMonth != null) {
                    TreeItem tiYear = tiMonth.getParent();
                    String endDate = tiYear.getValue().toString() + "." + tiMonth.getValue().toString() + "." + _selectedItem.getValue();
                    String startDate = startItem.getText();
                    startItem.setText("Set Startdate");
                    log("End date : " + endDate + " selected");
                    Tab tab = (Tab) tpRoot.getSelectionModel().getSelectedItem();
                    log("Selected tab : " + tab.getText());
                    if (tab.getText().toString().equals("Statistics")) {
                        concatBandwidth(startDate, endDate);
                    }
                }
            }
        });
        datePeriod.getItems().addAll(startItem, stopItem);
        String dir = _model.getCsvDir();
        lModel.setText(_model.getName());
        tfLog.setText(dir);
        log("Selected dir : " + dir);
        List<String> alFiles = _model.getSortedERDDataFiles();
        String lasts0 = "";
        String lasts1 = "";
        String lasts2 = "";
        TreeItem<String> item0 = null;
        TreeItem<String> item1 = null;
        TreeItem<String> item2 = null;
        for (String fileName : alFiles) {
            String[] s = fileName.split("\\.");            
            if (!s[0].equals(lasts0)) {
                item0 = new TreeItem(s[0]);
                rootItem.getChildren().add(item0);
                lasts0 = s[0];
            }
            if (!s[1].equals(lasts1)) {
                item1 = new TreeItem(s[1]);
                item0.getChildren().add(item1);
                lasts1 = s[1];
            }
            if (!s[2].equals(lasts2)) {
                item2 = new TreeItem(s[2]);
                item1.getChildren().add(item2);
                lasts2 = s[2];
            }
        }
        // TreeView - listen to selection change
        tvStatistic.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Tab tab = (Tab) tpRoot.getSelectionModel().getSelectedItem();
                log("Selected tab : " + tab.getText());
                if (tab.getText().equals("Statistics"))  {
                    _selectedItem = (TreeItem)newValue;
                    updateChart();
                }
            }        
        });
        // TreeView - listen to key event
        tvStatistic.setOnKeyPressed(event->{
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP ) {
                TreeItem tiOld = (TreeItem)tvStatistic.getSelectionModel().getSelectedItem();
                int i  = tvStatistic.getSelectionModel().getSelectedIndex();
                System.out.println("Item : " + i);
                System.out.println("Item : " + tiOld.getValue().toString());
                System.out.println("Key was pressed");
                if (event.getCode() == KeyCode.DOWN) {
                    tvStatistic.getSelectionModel().selectNext();
                } else {
                    tvStatistic.getSelectionModel().selectPrevious();
                }
            }
        });
        if (cbFrequence.getItems().isEmpty()) {
            cbFrequence.getItems().addAll("1min", "2min", "5min", "15min", "30min", "60min");
            cbFrequence.getSelectionModel().selectFirst();
        }
        tvStatistic.setContextMenu(datePeriod);
        tvStatistic.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>(){
            @Override
            public void handle(ContextMenuEvent event) {
                log("Handle context menu");
                datePeriod.show(tvStatistic, event.getScreenX(), event.getScreenY());
            }            
        });
    }
    //---------------------------------------------------------
    // Reintialize neccessary variables to start new statistic
    private void clear() {
        log("Controller: clear");
        if (_lastStage != null) {
            _lastStage.close();
            _lastStage = null;
        }
        for (Stage st : _alStages) {
            st.close();
        }
        _alStages.clear();
        _bUpdateLastLineChart = false;
        bLinearReg.setDisable(true);
        bMaxPerHour.setDisable(true);
        lDate.setText("");
        lBandWidth.setText("");
        lTotal.setText("");
    }
    
    //--------------------------------
    // Central logging of information
    private void log(String s) {
        if (_LOGGER == null) {
            _LOGGER = Logger.getInstance();
        }
        if (taLogger != null && _LOGGER != null) {
           _LOGGER.setFXMLTextAreaLogger(taLogger);
        }
        if (taLogger != null) {
            _LOGGER.log(s);
        } else {
            System.out.println("Missing initialize Logging.");
        }
    }

    //----------------------------------------------------------------
    // Click in chart
    // Result is value of x- and y-axis. Result is bandwidth and time
    private void lineChartsActions(LineChart<Number, Number> lineChart, NumberAxis xAxis, NumberAxis yAxis) {
        lineChart.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                double x = xAxis.sceneToLocal(mouseSceneCoords).getX();
                double y = yAxis.sceneToLocal(mouseSceneCoords).getY();
                int ix = xAxis.getValueForDisplay(x).intValue();
                String iy = Integer.toString(yAxis.getValueForDisplay(y).intValue());
                LocalTime lt = LocalTime.of(0, 0,0  );
                lt = lt.plusMinutes(ix);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "H:mm:ss" );
                String sDate = formatter.format( lt );
                lDate.setText(sDate);
                lBandWidth.setText(iy);
            }
        });
    }
    //---------------------------------------------------
    // Parse bandwidth to long and reduce unit to MBit/s
    private long parseBandwith(int iLine, int unit, String s) {
        Double dValue;
        long   value;
        if (unit == 0) {
            try {
                dValue = Double.parseDouble(s)  / 1000 / 1000; // MBits/s;
                value  =  dValue.intValue();
            } catch (NumberFormatException ex) {
                log("Max value set for line " + iLine);
                value = Integer.MAX_VALUE / 1000 / 1000; // MBits/s;
            }
        } else {
            value = Integer.parseInt(s);
        }
        return value;
    }
    //-------------------------------------------------
    // Concatenate different dates to one single chart
    private void concatBandwidth(String startDate, String endDate) {
        log("Calculate period");
        File fStart = _model.getCsvDataFiles(startDate);
        File fEnd   = _model.getCsvDataFiles(endDate);
        log("Startfile : " + fStart.getName());
        log("Endfile : " + fEnd.getName());
        ArrayList<File> alFiles = _model.getFilesBetween(startDate, endDate);
        ERDStatistic erdTotal = null;
        for (File f : alFiles) {
            log("Found : " + f.getName());
            if (erdTotal == null) {
                            erdTotal = new ERDStatistic(f);
            } else {
                ERDStatistic erd = new ERDStatistic(f);
                erdTotal.add(erd);
            }
        }
        Series sIn  = erdTotal.getSeriesIn();
        Series sOut = erdTotal.getSeriesOut();
        log("Total size for period : " + sIn.getData().size());
        // Definition of line chart  
        final NumberAxis                 xAxis         = new NumberAxis();   
        final NumberAxis                 yAxis         = new NumberAxis();   
        final LineChart<Number,Number>   lineChart     = new LineChart<>(xAxis,yAxis); 
        lineChart.getData().clear();
        lineChart.getData().addAll(sIn, sOut);
        lineChart.setTitle(startDate + " - " + endDate);
        setLayoutLineChart(lineChart, lineChart.getTitle(), false, bpCenterChart.getWidth(), bpCenterChart.getHeight(), 0.0, 0.0);
        // Check Unit fron data file  
        String sUnit;
        if (_unit == 0) {
            sUnit = " (in MBit/s)"; 
        } else {
            sUnit = " (in %)"; 
        }
        // Decribe Chart: Date and Unit                  
        bpCenterChart.getChildren().clear();
        bpCenterChart.getChildren().add(lineChart);
        lineChartsActions(lineChart, xAxis, yAxis);
        _selectedLineChart = lineChart;
        linRegLineChart    = lineChart;
    }
    //----------------------------------
    // Build array of points from chart
    private void getDataOfSeries(LineChart<Number, Number> linRegLineChart, double[] aXin, double[] aYin, double[] aXout, double[] aYout) {
        ObservableList<Series<Number, Number>> olLineChart = linRegLineChart.getData();
        Series seriesIn  = olLineChart.get(0);
        Series seriesOut = olLineChart.get(1);
        ObservableList olSeriesIn  = seriesIn.getData();
        ObservableList olSeriesOut = seriesOut.getData();
        // Define array of data points and fill arrays from line chart in loop
        int size = olSeriesIn.size();
        for (int i=0; i<size; i++) {
            Data data = (Data)olSeriesIn.get(i);
            aXin[i] = (double)Integer.valueOf(data.getXValue().toString());
            aYin[i] = (double)Integer.valueOf(data.getYValue().toString());
            data = (Data)olSeriesOut.get(i);
            aXout[i] = (double)Integer.valueOf(data.getXValue().toString());
            aYout[i] = (double)Integer.valueOf(data.getYValue().toString());
        }
    }   

    private void getMaxPerHourDataOfSeries(double[] aXin, double[] aYin, double[] aXout, double[] aYout) {
        
    }
}