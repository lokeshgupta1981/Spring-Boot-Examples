package com.howtodoinjava.demo.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/*@JacksonXmlRootElement(localName = "items")*/
@XmlRootElement(name = "items")
@XmlAccessorType(XmlAccessType.FIELD)
public class Items {

  /*@JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "item")*/
  @XmlElement(name = "item")
  List<Item> items;
}
