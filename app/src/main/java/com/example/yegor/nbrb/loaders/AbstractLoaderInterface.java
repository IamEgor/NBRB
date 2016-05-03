package com.example.yegor.nbrb.loaders;

import java.io.IOException;

public interface AbstractLoaderInterface<T> {
    T action() throws IOException;
}
