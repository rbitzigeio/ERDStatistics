/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RBO-VS-Admin
 */
public class RBU {
    
    private String  ID; // in db UUID
    private int     LS;
    
    private String  artikel;
    private String  LSName;
    private String  kundenOrganisation;
    private String  kostenstelle;
    private String  artikelBezeichnung;
    private String  mengenEinheit;
    private String  abrechnungsBezug;
    private String  bestellung;
    private String  itService;
    private String  technischesObjekt;
    private String  bemerkung;
    
    private double menge;
    private double preis;
    private double totalEUR;
    
    public RBU() {
    }

    /**
     * @return the ID
     */
    public String getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * @return the LS
     */
    public int getLS() {
        return LS;
    }

    /**
     * @param LS the LS to set
     */
    public void setLS(int LS) {
        this.LS = LS;
    }

    /**
     * @return the artikel
     */
    public String getArtikel() {
        return artikel;
    }

    /**
     * @param artikel the artikel to set
     */
    public void setArtikel(String artikel) {
        this.artikel = artikel;
    }

    /**
     * @return the LSName
     */
    public String getLSName() {
        return LSName;
    }

    /**
     * @param LSName the LSName to set
     */
    public void setLSName(String LSName) {
        this.LSName = LSName;
    }

    /**
     * @return the kundenOrganisation
     */
    public String getKundenOrganisation() {
        return kundenOrganisation;
    }

    /**
     * @param kundenOrganisation the kundenOrganisation to set
     */
    public void setKundenOrganisation(String kundenOrganisation) {
        this.kundenOrganisation = kundenOrganisation;
    }

    /**
     * @return the kostenstelle
     */
    public String getKostenstelle() {
        return kostenstelle;
    }

    /**
     * @param kostenstelle the kostenstelle to set
     */
    public void setKostenstelle(String kostenstelle) {
        this.kostenstelle = kostenstelle;
    }

    /**
     * @return the artikelBezeichnung
     */
    public String getArtikelBezeichnung() {
        return artikelBezeichnung;
    }

    /**
     * @param artikelBezeichnung the artikelBezeichnung to set
     */
    public void setArtikelBezeichnung(String artikelBezeichnung) {
        this.artikelBezeichnung = artikelBezeichnung;
    }

    /**
     * @return the mengenEinheit
     */
    public String getMengenEinheit() {
        return mengenEinheit;
    }

    /**
     * @param mengenEinheit the mengenEinheit to set
     */
    public void setMengenEinheit(String mengenEinheit) {
        this.mengenEinheit = mengenEinheit;
    }

    /**
     * @return the abrechnungsBezug
     */
    public String getAbrechnungsBezug() {
        return abrechnungsBezug;
    }

    /**
     * @param abrechnungsBezug the abrechnungsBezug to set
     */
    public void setAbrechnungsBezug(String abrechnungsBezug) {
        this.abrechnungsBezug = abrechnungsBezug;
    }

    /**
     * @return the bestellung
     */
    public String getBestellung() {
        return bestellung;
    }

    /**
     * @param bestellung the bestellung to set
     */
    public void setBestellung(String bestellung) {
        this.bestellung = bestellung;
    }

    /**
     * @return the itService
     */
    public String getItService() {
        return itService;
    }

    /**
     * @param itService the itService to set
     */
    public void setItService(String itService) {
        this.itService = itService;
    }

    /**
     * @return the technischesObjekt
     */
    public String getTechnischesObjekt() {
        return technischesObjekt;
    }

    /**
     * @param technischesObjekt the technischesObjekt to set
     */
    public void setTechnischesObjekt(String technischesObjekt) {
        this.technischesObjekt = technischesObjekt;
    }

    /**
     * @return the bemerkung
     */
    public String getBemerkung() {
        return bemerkung;
    }

    /**
     * @param bemerkung the bemerkung to set
     */
    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * @return the menge
     */
    public double getMenge() {
        return menge;
    }

    /**
     * @param menge the menge to set
     */
    public void setMenge(double menge) {
        this.menge = menge;
    }

    /**
     * @return the preis
     */
    public double getPreis() {
        return preis;
    }

    /**
     * @param preis the preis to set
     */
    public void setPreis(double preis) {
        this.preis = preis;
    }

    /**
     * @return the totalEUR
     */
    public double getTotalEUR() {
        return totalEUR;
    }

    /**
     * @param totalEUR the totalEUR to set
     */
    public void setTotalEUR(double totalEUR) {
        this.totalEUR = totalEUR;
    }

    public static boolean isLoaded(String month) throws Exception {
       boolean isLoaded = false;
       isLoaded = SQLCommunication.checkRBUAbrechnungsmonat(month);
       return isLoaded;
    }
    
    public void insertRBUAbrechnungsmonat(String month, String fileName) throws Exception {
        SQLCommunication.insertRBUAbrechnungsmonat(month, fileName);
    }
    
    public void insertRBUPosition() throws Exception {
        SQLCommunication.insertRBUPosition(this.getKundenOrganisation(),
                                           this.getKostenstelle(),
                                           this.getLS(),
                                           this.getLSName(),
                                           this.getArtikel(),
                                           this.getArtikelBezeichnung(),
                                           this.getMengenEinheit(),
                                           this.getAbrechnungsBezug(),
                                           this.getBestellung(),
                                           this.getItService(),
                                           this.getTechnischesObjekt(),
                                           this.getBemerkung(),
                                           this.getMenge(),
                                           this.getPreis(),
                                           this.getTotalEUR());
    }
    
    public static List<RBU> getListOfRBU() throws Exception {
        List<RBU> listOfRBU = new ArrayList();
        
        return listOfRBU;
    }
}

