package com.howtodoinjava.jackson.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.howtodoinjava.jackson.model.Grams;

import java.io.IOException;

/** Jackson 2 custom serializer. */
public class GramsSerializer2 extends JsonSerializer<Grams> {

  @Override
  public void serialize(Grams grams, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeString(grams.value() + " g");
  }
}
