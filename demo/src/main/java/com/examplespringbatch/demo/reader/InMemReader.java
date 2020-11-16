package com.examplespringbatch.demo.reader;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InMemReader extends AbstractItemStreamItemReader {

    List<Integer> integerList = Stream.of(1, 2, 3, 4, 5, 6, 7).collect(Collectors.toList());
    int i = 0;

    @Override
    public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        Integer nextItem = null;
        if (i < integerList.size()) {
            nextItem = integerList.get(i);
            i++;
        } else {
            i = 0;
        }

        return nextItem;
    }
}
