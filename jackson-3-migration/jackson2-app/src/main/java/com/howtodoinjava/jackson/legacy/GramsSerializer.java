package com.howtodoinjava.jackson.legacy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class GramsSerializer extends JsonSerializer<Integer> {

  @Override
  public void serialize(Integer grams, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeString(grams + " g");
  }
}
