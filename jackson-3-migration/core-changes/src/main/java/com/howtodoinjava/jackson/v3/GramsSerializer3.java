package com.howtodoinjava.jackson.v3;

import com.howtodoinjava.jackson.model.Grams;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/** Jackson 3 custom serializer: ValueSerializer and SerializationContext. */
public class GramsSerializer3 extends ValueSerializer<Grams> {

  @Override
  public void serialize(Grams grams, JsonGenerator gen, SerializationContext ctxt)
      throws JacksonException {
    gen.writeString(grams.value() + " g");
  }
}
