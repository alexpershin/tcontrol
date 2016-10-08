package com.embarcadero.interview.handson;

import java.util.List;

/**
 * Interface to load data from file.
 */
public interface CSVLoader {
    List<User> load(String fileName);
}
