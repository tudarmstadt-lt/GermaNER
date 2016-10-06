

/* First created by JCasGen Wed Oct 05 18:48:49 CEST 2016 */
package de.tu.darmstadt.lt.ner.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Oct 06 11:48:26 CEST 2016
 * XML source: /home/seid/git/GermaNER/germaner/src/main/resources/desc/type/DocumentNumber.xml
 * @generated */
public class DocumentNumber extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentNumber.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected DocumentNumber() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DocumentNumber(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DocumentNumber(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DocumentNumber(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Number

  /** getter for Number - gets 
   * @generated
   * @return value of the feature 
   */
  public long getNumber() {
    if (DocumentNumber_Type.featOkTst && ((DocumentNumber_Type)jcasType).casFeat_Number == null)
      jcasType.jcas.throwFeatMissing("Number", "de.tu.darmstadt.lt.ner.types.DocumentNumber");
    return jcasType.ll_cas.ll_getLongValue(addr, ((DocumentNumber_Type)jcasType).casFeatCode_Number);}
    
  /** setter for Number - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumber(long v) {
    if (DocumentNumber_Type.featOkTst && ((DocumentNumber_Type)jcasType).casFeat_Number == null)
      jcasType.jcas.throwFeatMissing("Number", "de.tu.darmstadt.lt.ner.types.DocumentNumber");
    jcasType.ll_cas.ll_setLongValue(addr, ((DocumentNumber_Type)jcasType).casFeatCode_Number, v);}    
   
    
  //*--------------*
  //* Feature: Text

  /** getter for Text - gets 
   * @generated
   * @return value of the feature 
   */
  public String getText() {
    if (DocumentNumber_Type.featOkTst && ((DocumentNumber_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "de.tu.darmstadt.lt.ner.types.DocumentNumber");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentNumber_Type)jcasType).casFeatCode_Text);}
    
  /** setter for Text - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setText(String v) {
    if (DocumentNumber_Type.featOkTst && ((DocumentNumber_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "de.tu.darmstadt.lt.ner.types.DocumentNumber");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentNumber_Type)jcasType).casFeatCode_Text, v);}    
  }

    