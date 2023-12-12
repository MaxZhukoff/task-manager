package com.manager.mapper;

public interface Mapper<F, T> {

    T map(F object);
}
