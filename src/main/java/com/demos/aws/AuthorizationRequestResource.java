package com.demos.aws;

import org.hibernate.validator.constraints.Length;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@ApplicationScoped
public class AuthorizationRequestResource {

  public void validatePolicyStoreId(
      @NotBlank(message = "Policy Store Id is required.") 
      @Length(min = 1, max = 200, message = "Policy Store Id string should be between 1 and 200 characters.") 
      @Pattern(regexp = "[a-zA-Z0-9-]*", message = "Policy Store Id has invalid format.") 
      String policyStoreId) {

  }

  public void validatePolicyPrincipal(@Valid PolicyPrincipalResource principal) {

  }

  public void validatePolicyAction(@Valid PolicyActionResource action) {

  }

  public void validatePolicyResource(@Valid PolicyResourceResource resource) {

  }
}
