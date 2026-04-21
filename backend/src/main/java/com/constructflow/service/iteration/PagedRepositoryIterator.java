package com.constructflow.service.iteration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class PagedRepositoryIterator<T> implements Iterator<T> {

    private final Function<Pageable, Page<T>> pageFetcher;
    private final int pageSize;
    private Page<T> currentPage;
    private Iterator<T> inner = Collections.emptyIterator();
    private int nextPageIndex = 0;

    public PagedRepositoryIterator(Function<Pageable, Page<T>> pageFetcher, int pageSize) {
        this.pageFetcher = pageFetcher;
        this.pageSize = pageSize;
        advance();
    }

    private void advance() {
        currentPage = pageFetcher.apply(PageRequest.of(nextPageIndex++, pageSize));
        inner = currentPage.getContent().iterator();
    }

    @Override
    public boolean hasNext() {
        if (inner.hasNext()) return true;
        if (currentPage.hasNext()) {
            advance();
            return inner.hasNext();
        }
        return false;
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        return inner.next();
    }
}
