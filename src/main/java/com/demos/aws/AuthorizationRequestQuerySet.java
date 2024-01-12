package com.demos.aws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthorizationRequestQuerySet {
  private HashMap<String, ArrayList<String>> querySet;

  public AuthorizationRequestQuerySet() {
    this.querySet = new HashMap<String, ArrayList<String>>();
  }

  public boolean keyExists(String key) {
    return querySet.containsKey(key);
  }

  public void addQuery(String key, String value) {
    if (querySet.get(key) == null) {
      querySet.put(key, new ArrayList<String>(Arrays.asList(value)));
    } else {
      querySet.get(key).add(value);
    }
  }

  public List<String> getQueries(String key) {
    return querySet.get(key);
  }

  public void removeKey(String key) {
    querySet.remove(key);
  }
}
