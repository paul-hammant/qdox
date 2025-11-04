module org.openapitools.jackson.nullable {
  requires static java.validation;

  exports org.openapitools.jackson.nullable;

  provides javax.validation.valueextraction.ValueExtractor with
      org.openapitools.jackson.nullable.MyOptionalExtractor;
}
