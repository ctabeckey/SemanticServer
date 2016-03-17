//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.17 at 10:38:20 AM EDT 
//


package com.paypal.credit.context.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BeanType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BeanType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element name="constructor-arg" type="{http://credit.paypal.com/v1/schema/ctx}ConstructorArgType"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="class" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="scope" type="{http://credit.paypal.com/v1/schema/ctx}ScopeType" /&gt;
 *       &lt;attribute name="artifact" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BeanType", propOrder = {
    "constructorArg"
})
public class BeanType {

    @XmlElement(name = "constructor-arg")
    protected List<ConstructorArgType> constructorArg;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "class", required = true)
    protected String clazz;
    @XmlAttribute(name = "scope")
    protected ScopeType scope;
    @XmlAttribute(name = "artifact")
    protected String artifact;

    /**
     * Gets the value of the constructorArg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the constructorArg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConstructorArg().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstructorArgType }
     * 
     * 
     */
    public List<ConstructorArgType> getConstructorArg() {
        if (constructorArg == null) {
            constructorArg = new ArrayList<ConstructorArgType>();
        }
        return this.constructorArg;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link ScopeType }
     *     
     */
    public ScopeType getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScopeType }
     *     
     */
    public void setScope(ScopeType value) {
        this.scope = value;
    }

    /**
     * Gets the value of the artifact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArtifact() {
        return artifact;
    }

    /**
     * Sets the value of the artifact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArtifact(String value) {
        this.artifact = value;
    }

}
