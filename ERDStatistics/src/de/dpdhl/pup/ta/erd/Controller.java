/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
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
    @FXML private TextArea   taLogger;
    
    private int              _frequence = 1;
    private int              _unit = 0; // Default unit Bits/s, 1 = %
    private double           _WindowX;
    private double           _WindowY;
    private boolean          _bUpdateLastLineChart = false;   
    private File             _lastFile;    
    private Model            _model; 
    
    LineChart<Number,Number> lastLineChart;
    LineChart<Number,Number> linRegLineChart;
    boolean                  bUpdateLastLineChart = false;
    Stage                    lastStage;
    File                     lastFile;
    ArrayList<Stage>         alStages = new ArrayList<>();

    private Stage                       _parentWindow;
    private Stage                       _lastStage;
    private ArrayList<Stage>            _alStages = new ArrayList<>();
    private TreeItem<String>            _selectedItem;
    private LineChart<Number,Number>    _lastLineChart;
    private LineChart<Number,Number>    _selectedLineChart;

    private static Logger               _LOGGER;

    //------------------------------------
    // Exit application and close windows
    //
    @FXML private void handleExitAction(ActionEvent event) {
        log("Exit");
        System.exit(0);
    }
    //-------------------
    // Linear Regression
    //
    @FXML private void handleLinRegAction(ActionEvent event) {
        if (linRegLineChart != null) {
            log("Calculate linear regression for chart : " + linRegLineChart.getTitle());
            final NumberAxis                 xAxisLr        = new NumberAxis();   
            final NumberAxis                 yAxisLr        = new NumberAxis();   
            final LineChart<Number,Number>   lineChartLr    = new LineChart<>(xAxisLr,yAxisLr); // LineChart in main window
            ObservableList<Series<Number, Number>> olLineChart = linRegLineChart.getData();
            Series seriesIn  = olLineChart.get(0);
            Series seriesOut = olLineChart.get(1);
            ObservableList olSeriesIn  = seriesIn.getData();
            ObservableList olSeriesOut = seriesOut.getData();
            // Define array of data points and fill arrays from line chart in loop
            double[] aXin  = new double[olSeriesIn.size()];
            double[] aYin  = new double[olSeriesIn.size()];
            double[] aXout = new double[olSeriesIn.size()];
            double[] aYout = new double[olSeriesIn.size()];
            int size = olSeriesIn.size();
            for (int i=0; i<size; i++) {
                Data data = (Data)olSeriesIn.get(i);
                aXin[i] = (double)Integer.valueOf(data.getXValue().toString());
                aYin[i] = (double)Integer.valueOf(data.getYValue().toString());
                data = (Data)olSeriesOut.get(i);
                aXout[i] = (double)Integer.valueOf(data.getXValue().toString());
                aYout[i] = (double)Integer.valueOf(data.getYValue().toString());
            }
            // Calculate linear regression 
            LinearRegression lrIn  = new LinearRegression(aXin, aYin);
            LinearRegression lrOut = new LinearRegression(aXout, aYout);
            Series seriesLRIn  = new Series();
            Series seriesLROut = new Series();
            // Only first and last point neccessary for definition of line
            // Y = intercept + x * slope // y = y(0) + x * Steigung
            seriesLRIn.getData().add(new XYChart.Data(0,lrIn.intercept())); 
            seriesLRIn.getData().add(new XYChart.Data(size*_frequence,lrIn.intercept() + size*_frequence*lrIn.slope()));    
            seriesLROut.getData().add(new XYChart.Data(0, lrOut.intercept()));
            seriesLROut.getData().add(new XYChart.Data(size*_frequence, lrOut.intercept() + size*_frequence*lrOut.slope()));
            seriesLRIn.setName("LR In-Bound");
            seriesLROut.setName("LR Out-Bound");
            linRegLineChart.getData().addAll(seriesLRIn, seriesLROut);
            String lrTitle = linRegLineChart.getTitle() + " LR)";
            linRegLineChart.setTitle(lrTitle);
        }
    }
    //-------------------
    // Load initial data
    //
    public void loadData() {
        tfLog.setText("Load data");
        log("Load Data");
        loadProjectTree();
        loadModelMenues();
        bLinearReg.setDisable(true);
    }
    //--------------------------------------------------------------------------------------
    // Methode wird nach Änderung des Verzeichnisses mit Statistikdaten mehrmals aufgerufen.
    // Grund unbekannt!!!
    //
    private void updateChart() {
        if (_selectedItem != null) {
            bLinearReg.setDisable(false);
            TreeItem<String> selectedDDItem   = (TreeItem<String>) _selectedItem;
            TreeItem<String> selectedMMItem   = (TreeItem<String>) selectedDDItem.getParent();
            TreeItem<String> selectedYYYYItem = (TreeItem<String>) selectedMMItem.getParent();

            String fileName = selectedYYYYItem.getValue() + "." + selectedMMItem.getValue() + "." + selectedDDItem.getValue();
            File f = _model.getCsvDataFiles(fileName); 
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
                lineChart.setTitle("Bandbreite : " + selectedDDItem.getValue() + "." + selectedMMItem.getValue() + "." + selectedYYYYItem.getValue() + sUnit);
                bpCenterChart.getChildren().clear();
                bpCenterChart.getChildren().add(lineChart);
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
    //
    private Series[] createChart(File f) {
        Series  seriesIn  = new Series();
        Series  seriesOut = new Series();
        Series[] series = {seriesIn, seriesOut};
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
                        int valueIn  = Integer.parseInt(s0[0]);
                        int valueOut = Integer.parseInt(s1[0]);
                        sumValueIn   = sumValueIn  + valueIn; 
                        sumValueOut  = sumValueOut + valueOut;
                        iSum         = iSum + i;
                        if (i % _frequence == 0) {
                            if (_unit == 0) {                              
                                sumValueIn  = sumValueIn / 1000 / 1000; // MBits/s
                                //valueIn     = valueIn / 1000 / 1000; // MBits/s
                                sumValueOut = sumValueOut / 1000 / 1000; // MBits/s
                                //valueOut    = valueOut / 1000 / 1000; // MBits/s
                            }
                            seriesIn.getData().add(new XYChart.Data(iSum/_frequence, sumValueIn/_frequence)); // Average
                            //seriesIn.getData().add(new XYChart.Data(i, valueIn));
                            seriesOut.getData().add(new XYChart.Data(iSum/_frequence, sumValueOut/_frequence)); // Average
                            //seriesOut.getData().add(new XYChart.Data(i, valueOut));
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
                    break; // WHILE
                }
            } 
        } catch (Exception ex) {  
            log("Line : " + i + inString);
            log("Error reading file: " + ex.getMessage()) ;
        } 
        return series;    
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log("Controller Initialize");
    }       

    private String setTVItem(String s, String lasts, TreeItem<String> item, TreeItem<String> parentItem) {
        if (!s.equals(lasts)) {
            item.setValue(s);
            parentItem.getChildren().add(item);
            lasts = s;
        }
        return lasts;
    }
    //-------------------------------------
    // Action to change value of frequence
    //
    @FXML private void handleFrequenceAction(ActionEvent event) {
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
    //
    public void setWindow(double x, double y) {
        _WindowX = x;
        _WindowY = y;
    }

    public void setParentWindow(Stage stage) {
        _parentWindow = stage;
    }
    //----------------------------------------------------------------------------
    // Data of actual line chart and last line chart is used to compare this data
    // and visualize data and diff of data in seperated window
    //
    private void calculateLineCharts(LineChart<Number, Number> lineChart, 
                                     LineChart<Number, Number> actLineChart, 
                                     LineChart<Number, Number> diffLineChart, 
                                     LineChart<Number, Number> _lastLineChart) {
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
    //
    private void setLayoutLineChart(LineChart lc, String title, boolean b, double width, double height, double x, double y) {
        lc.setTitle(title);
        lc.setCreateSymbols(b);
        lc.setPrefSize(width, height);
        lc.setLayoutX(x);
        lc.setLayoutY(y);    
    }
    //---------------------------------
    // Load menue of project into menu
    //
    private void loadModelMenues() {
        HashMap<String, String> hm = _model.getCsvDirs();
        for (String key : hm.keySet()) {
            String v = hm.get(key);
            MenuItem mi = new MenuItem(v);
            log("Model inserted " + v);
            mData.getItems().add(mi);
            mi.addEventHandler(EventType.ROOT, (Event event) -> {
                MenuItem mii = (MenuItem)event.getSource();
                _model.setCsvDir(mii.getText());
                log("Model changed to " + mii.getText());
                bpCenterChart.getChildren().clear();
                clear();
                loadProjectTree();
            });
        }
    }
    //-----------------------------------------
    // Load tree for selected platform/project
    //
    private void loadProjectTree() {
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
            
        }
        String dir = _model.getCsvDir();
        tfLog.setText(dir);
        log("Selected dir : " + dir);
        List<String> alFiles = _model.getSortedERDDataFiles();
        String lasts0 = "";
        String lasts1 = "";
        String lasts2 = "";
        TreeItem<String> item0 = new TreeItem();
        TreeItem<String> item1 = new TreeItem();
        TreeItem<String> item2 = new TreeItem();
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
                _selectedItem = (TreeItem)newValue;
                updateChart();
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
                //TreeItem tiNew = (TreeItem)tvStatistic.getSelectionModel().getSelectedItem();
                //TreeModificationEvent<String> tme = new TreeModificationEvent<>(TreeItem.valueChangedEvent(), tiNew);
                //Event.fireEvent(tiNew, tme);
           }
        });
        if (cbFrequence.getItems().isEmpty()) {
            cbFrequence.getItems().addAll("1min", "2min", "5min", "15min", "30min", "60min");
            cbFrequence.getSelectionModel().selectFirst();
        }
    }
    //---------------------------------------------------
    // Clear data to initial point by changing selection
    //
    private void clear() {
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
    }
    //--------------------------------
    // Central logging of information
    //
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

}
