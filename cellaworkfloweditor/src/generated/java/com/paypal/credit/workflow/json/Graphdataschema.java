package com.paypal.credit.workflow.json;
import java.util.List;
import java.util.Set;
public @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"format_version","generated_by","target_cytoscapejs_version","data","elements"}) class Graphdataschema {
  private @com.fasterxml.jackson.annotation.JsonProperty("format_version") String formatVersion;
  private @com.fasterxml.jackson.annotation.JsonProperty("generated_by") String generatedBy;
  private @com.fasterxml.jackson.annotation.JsonProperty("target_cytoscapejs_version") String targetCytoscapejsVersion;
  private @javax.validation.Valid @com.fasterxml.jackson.annotation.JsonProperty("data") Data data;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"selected","Annotations","shared_name","SUID","name"}) class Data {
    private @com.fasterxml.jackson.annotation.JsonProperty("selected") Boolean selected;
    private @com.fasterxml.jackson.databind.annotation.JsonDeserialize(as=java.util.ArrayList.class) List<String> Annotations;
    private @com.fasterxml.jackson.annotation.JsonProperty("shared_name") String sharedName;
    private @com.fasterxml.jackson.annotation.JsonProperty("SUID") Long suid;
    private @com.fasterxml.jackson.annotation.JsonProperty("name") String name;
    public Data(    Boolean selected,    List<String> Annotations,    String sharedName,    Long suid,    String name){
      this.selected=selected;
      this.Annotations=Annotations;
      this.sharedName=sharedName;
      this.suid=suid;
      this.name=name;
    }
    public Data(){
      this.selected=null;
      this.Annotations=null;
      this.sharedName=null;
      this.suid=null;
      this.name=null;
    }
    public Boolean getSelected(){
      return selected;
    }
    public List<String> getAnnotations(){
      return Annotations;
    }
    public String getSharedName(){
      return sharedName;
    }
    public Long getSuid(){
      return suid;
    }
    public String getName(){
      return name;
    }
    public void setSelected(    Boolean selected){
      this.selected=selected;
    }
    public void setAnnotations(    List<String> Annotations){
      this.Annotations=Annotations;
    }
    public void setSharedName(    String sharedName){
      this.sharedName=sharedName;
    }
    public void setSuid(    Long suid){
      this.suid=suid;
    }
    public void setName(    String name){
      this.name=name;
    }
  }
  private @javax.validation.Valid @com.fasterxml.jackson.annotation.JsonProperty("elements") Elements elements;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"nodes","edges"}) class Elements {
    private @com.fasterxml.jackson.databind.annotation.JsonDeserialize(as=java.util.ArrayList.class) List<Nodetype> nodes;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"data","position","removed","selected","selectable","locked","grabbable","classes"}) class Nodetype {
      private @javax.validation.Valid @com.fasterxml.jackson.annotation.JsonProperty("data") Data data;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"id","weight","processor"}) class Data {
        private @com.fasterxml.jackson.annotation.JsonProperty("id") String id;
        private @com.fasterxml.jackson.annotation.JsonProperty("weight") Long weight;
        private @com.fasterxml.jackson.annotation.JsonProperty("processor") String processor;
        public Data(        String id,        Long weight,        String processor){
          this.id=id;
          this.weight=weight;
          this.processor=processor;
        }
        public Data(){
          this.id=null;
          this.weight=null;
          this.processor=null;
        }
        public String getId(){
          return id;
        }
        public Long getWeight(){
          return weight;
        }
        public String getProcessor(){
          return processor;
        }
        public void setId(        String id){
          this.id=id;
        }
        public void setWeight(        Long weight){
          this.weight=weight;
        }
        public void setProcessor(        String processor){
          this.processor=processor;
        }
      }
      private @javax.validation.Valid @com.fasterxml.jackson.annotation.JsonProperty("position") Position position;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"x","y"}) class Position {
        private @com.fasterxml.jackson.annotation.JsonProperty("x") Long x;
        private @com.fasterxml.jackson.annotation.JsonProperty("y") Long y;
        public Position(        Long x,        Long y){
          this.x=x;
          this.y=y;
        }
        public Position(){
          this.x=null;
          this.y=null;
        }
        public Long getX(){
          return x;
        }
        public Long getY(){
          return y;
        }
        public void setX(        Long x){
          this.x=x;
        }
        public void setY(        Long y){
          this.y=y;
        }
      }
      private @com.fasterxml.jackson.annotation.JsonProperty("removed") Boolean removed;
      private @com.fasterxml.jackson.annotation.JsonProperty("selected") Boolean selected;
      private @com.fasterxml.jackson.annotation.JsonProperty("selectable") Boolean selectable;
      private @com.fasterxml.jackson.annotation.JsonProperty("locked") Boolean locked;
      private @com.fasterxml.jackson.annotation.JsonProperty("grabbable") Boolean grabbable;
      private @com.fasterxml.jackson.annotation.JsonProperty("classes") String classes;
      public Nodetype(      Data data,      Position position,      Boolean removed,      Boolean selected,      Boolean selectable,      Boolean locked,      Boolean grabbable,      String classes){
        this.data=data;
        this.position=position;
        this.removed=removed;
        this.selected=selected;
        this.selectable=selectable;
        this.locked=locked;
        this.grabbable=grabbable;
        this.classes=classes;
      }
      public Nodetype(){
        this.data=null;
        this.position=null;
        this.removed=null;
        this.selected=null;
        this.selectable=null;
        this.locked=null;
        this.grabbable=null;
        this.classes=null;
      }
      public Data getData(){
        return data;
      }
      public Position getPosition(){
        return position;
      }
      public Boolean getRemoved(){
        return removed;
      }
      public Boolean getSelected(){
        return selected;
      }
      public Boolean getSelectable(){
        return selectable;
      }
      public Boolean getLocked(){
        return locked;
      }
      public Boolean getGrabbable(){
        return grabbable;
      }
      public String getClasses(){
        return classes;
      }
      public void setData(      Data data){
        this.data=data;
      }
      public void setPosition(      Position position){
        this.position=position;
      }
      public void setRemoved(      Boolean removed){
        this.removed=removed;
      }
      public void setSelected(      Boolean selected){
        this.selected=selected;
      }
      public void setSelectable(      Boolean selectable){
        this.selectable=selectable;
      }
      public void setLocked(      Boolean locked){
        this.locked=locked;
      }
      public void setGrabbable(      Boolean grabbable){
        this.grabbable=grabbable;
      }
      public void setClasses(      String classes){
        this.classes=classes;
      }
    }
    private @com.fasterxml.jackson.databind.annotation.JsonDeserialize(as=java.util.ArrayList.class) List<Edgetype> edges;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"data","removed","selected","selectable","locked","grabbable","classes"}) class Edgetype {
      private @javax.validation.Valid @com.fasterxml.jackson.annotation.JsonProperty("data") Data data;
public static @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL) @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true) @javax.annotation.Generated("com.paypal.credit.tools.jss2j") @com.fasterxml.jackson.annotation.JsonPropertyOrder({"id","weight","source","target"}) class Data {
        private @com.fasterxml.jackson.annotation.JsonProperty("id") String id;
        private @com.fasterxml.jackson.annotation.JsonProperty("weight") Long weight;
        private @com.fasterxml.jackson.annotation.JsonProperty("source") String source;
        private @com.fasterxml.jackson.annotation.JsonProperty("target") String target;
        public Data(        String id,        Long weight,        String source,        String target){
          this.id=id;
          this.weight=weight;
          this.source=source;
          this.target=target;
        }
        public Data(){
          this.id=null;
          this.weight=null;
          this.source=null;
          this.target=null;
        }
        public String getId(){
          return id;
        }
        public Long getWeight(){
          return weight;
        }
        public String getSource(){
          return source;
        }
        public String getTarget(){
          return target;
        }
        public void setId(        String id){
          this.id=id;
        }
        public void setWeight(        Long weight){
          this.weight=weight;
        }
        public void setSource(        String source){
          this.source=source;
        }
        public void setTarget(        String target){
          this.target=target;
        }
      }
      private @com.fasterxml.jackson.annotation.JsonProperty("removed") Boolean removed;
      private @com.fasterxml.jackson.annotation.JsonProperty("selected") Boolean selected;
      private @com.fasterxml.jackson.annotation.JsonProperty("selectable") Boolean selectable;
      private @com.fasterxml.jackson.annotation.JsonProperty("locked") Boolean locked;
      private @com.fasterxml.jackson.annotation.JsonProperty("grabbable") Boolean grabbable;
      private @com.fasterxml.jackson.annotation.JsonProperty("classes") String classes;
      public Edgetype(      Data data,      Boolean removed,      Boolean selected,      Boolean selectable,      Boolean locked,      Boolean grabbable,      String classes){
        this.data=data;
        this.removed=removed;
        this.selected=selected;
        this.selectable=selectable;
        this.locked=locked;
        this.grabbable=grabbable;
        this.classes=classes;
      }
      public Edgetype(){
        this.data=null;
        this.removed=null;
        this.selected=null;
        this.selectable=null;
        this.locked=null;
        this.grabbable=null;
        this.classes=null;
      }
      public Data getData(){
        return data;
      }
      public Boolean getRemoved(){
        return removed;
      }
      public Boolean getSelected(){
        return selected;
      }
      public Boolean getSelectable(){
        return selectable;
      }
      public Boolean getLocked(){
        return locked;
      }
      public Boolean getGrabbable(){
        return grabbable;
      }
      public String getClasses(){
        return classes;
      }
      public void setData(      Data data){
        this.data=data;
      }
      public void setRemoved(      Boolean removed){
        this.removed=removed;
      }
      public void setSelected(      Boolean selected){
        this.selected=selected;
      }
      public void setSelectable(      Boolean selectable){
        this.selectable=selectable;
      }
      public void setLocked(      Boolean locked){
        this.locked=locked;
      }
      public void setGrabbable(      Boolean grabbable){
        this.grabbable=grabbable;
      }
      public void setClasses(      String classes){
        this.classes=classes;
      }
    }
    public Elements(    List<Nodetype> nodes,    List<Edgetype> edges){
      this.nodes=nodes;
      this.edges=edges;
    }
    public Elements(){
      this.nodes=null;
      this.edges=null;
    }
    public List<Nodetype> getNodes(){
      return nodes;
    }
    public List<Edgetype> getEdges(){
      return edges;
    }
    public void setNodes(    List<Nodetype> nodes){
      this.nodes=nodes;
    }
    public void setEdges(    List<Edgetype> edges){
      this.edges=edges;
    }
  }
  public Graphdataschema(  String formatVersion,  String generatedBy,  String targetCytoscapejsVersion,  Data data,  Elements elements){
    this.formatVersion=formatVersion;
    this.generatedBy=generatedBy;
    this.targetCytoscapejsVersion=targetCytoscapejsVersion;
    this.data=data;
    this.elements=elements;
  }
  public Graphdataschema(){
    this.formatVersion=null;
    this.generatedBy=null;
    this.targetCytoscapejsVersion=null;
    this.data=null;
    this.elements=null;
  }
  public String getFormatVersion(){
    return formatVersion;
  }
  public String getGeneratedBy(){
    return generatedBy;
  }
  public String getTargetCytoscapejsVersion(){
    return targetCytoscapejsVersion;
  }
  public Data getData(){
    return data;
  }
  public Elements getElements(){
    return elements;
  }
  public void setFormatVersion(  String formatVersion){
    this.formatVersion=formatVersion;
  }
  public void setGeneratedBy(  String generatedBy){
    this.generatedBy=generatedBy;
  }
  public void setTargetCytoscapejsVersion(  String targetCytoscapejsVersion){
    this.targetCytoscapejsVersion=targetCytoscapejsVersion;
  }
  public void setData(  Data data){
    this.data=data;
  }
  public void setElements(  Elements elements){
    this.elements=elements;
  }
}
