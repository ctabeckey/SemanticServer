package com.paypal.credit.json;
import java.util.List;
import java.util.Set;
public @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder("processors") class Availableprocessorschema {
  private @com.fasterxml.jackson.databind.annotation.JsonDeserialize(as=java.util.ArrayList.class) List<Processor> processors;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"processor","configuration"}) class Processor {
    private @com.fasterxml.jackson.annotation.JsonProperty("processor") String processor;
    private @com.fasterxml.jackson.databind.annotation.JsonDeserialize(as=java.util.ArrayList.class) List<ProcessorProperty> configuration;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"key","type","description"}) class ProcessorProperty {
      private @com.fasterxml.jackson.annotation.JsonProperty("key") String key;
      private @com.fasterxml.jackson.annotation.JsonProperty("type") String type;
      private @com.fasterxml.jackson.annotation.JsonProperty("description") String description;
      public ProcessorProperty(      String key,      String type,      String description){
        this.key=key;
        this.type=type;
        this.description=description;
      }
      public ProcessorProperty(){
        this.key=null;
        this.type=null;
        this.description=null;
      }
      public String getKey(){
        return key;
      }
      public String getType(){
        return type;
      }
      public String getDescription(){
        return description;
      }
      public void setKey(      String key){
        this.key=key;
      }
      public void setType(      String type){
        this.type=type;
      }
      public void setDescription(      String description){
        this.description=description;
      }
    }
    public Processor(    String processor,    List<ProcessorProperty> configuration){
      this.processor=processor;
      this.configuration=configuration;
    }
    public Processor(){
      this.processor=null;
      this.configuration=null;
    }
    public String getProcessor(){
      return processor;
    }
    public List<ProcessorProperty> getConfiguration(){
      return configuration;
    }
    public void setProcessor(    String processor){
      this.processor=processor;
    }
    public void setConfiguration(    List<ProcessorProperty> configuration){
      this.configuration=configuration;
    }
  }
  public Availableprocessorschema(  List<Processor> processors){
    this.processors=processors;
  }
  public Availableprocessorschema(){
    this.processors=null;
  }
  public List<Processor> getProcessors(){
    return processors;
  }
  public void setProcessors(  List<Processor> processors){
    this.processors=processors;
  }
}
