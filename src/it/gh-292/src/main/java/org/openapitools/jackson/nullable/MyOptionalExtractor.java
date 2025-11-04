package org.openapitools.jackson.nullable;

import java.util.Optional;
import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;

@UnwrapByDefault
public class MyOptionalExtractor implements ValueExtractor<Optional<@ExtractedValue ?>> {

  @Override
  public void extractValues(Optional<?> originalValue, ValueReceiver receiver) {
    receiver.value(null, originalValue.get());
  }
}
