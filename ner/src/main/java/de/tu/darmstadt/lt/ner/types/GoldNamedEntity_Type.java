
/* First created by JCasGen Fri Jul 11 16:01:56 CEST 2014 */
package de.tu.darmstadt.lt.ner.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Fri Jul 11 16:01:56 CEST 2014
 * @generated */
public class GoldNamedEntity_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (GoldNamedEntity_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = GoldNamedEntity_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new GoldNamedEntity(addr, GoldNamedEntity_Type.this);
  			   GoldNamedEntity_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new GoldNamedEntity(addr, GoldNamedEntity_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = GoldNamedEntity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tu.darmstadt.lt.lqa.webapp.types.GoldNamedEntity");
 
  /** @generated */
  final Feature casFeat_NamedEntityType;
  /** @generated */
  final int     casFeatCode_NamedEntityType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNamedEntityType(int addr) {
        if (featOkTst && casFeat_NamedEntityType == null)
      jcas.throwFeatMissing("NamedEntityType", "de.tu.darmstadt.lt.lqa.webapp.types.GoldNamedEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_NamedEntityType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNamedEntityType(int addr, String v) {
        if (featOkTst && casFeat_NamedEntityType == null)
      jcas.throwFeatMissing("NamedEntityType", "de.tu.darmstadt.lt.lqa.webapp.types.GoldNamedEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_NamedEntityType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public GoldNamedEntity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_NamedEntityType = jcas.getRequiredFeatureDE(casType, "NamedEntityType", "uima.cas.String", featOkTst);
    casFeatCode_NamedEntityType  = (null == casFeat_NamedEntityType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_NamedEntityType).getCode();

  }
}



    