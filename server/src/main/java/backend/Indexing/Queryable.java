package backend.Indexing;

import backend.exceptions.recordHandlingExceptions.UndefinedQueryException;

import java.io.IOException;
import java.util.HashMap;

public interface Queryable {
    HashMap<Integer, Object> equalityQuery(Object key) throws UndefinedQueryException, IOException;
    HashMap<Integer, Object> rangeQuery(Object lowerBound, Object upperBound, boolean allowEqualityLower, boolean allowEqualityUpper) throws UndefinedQueryException, IOException;
    HashMap<Integer, Object> lesserQuery(Object upperBound, boolean allowEquality) throws UndefinedQueryException, IOException;
    HashMap<Integer, Object> greaterQuery(Object lowerBound, boolean allowEquality) throws UndefinedQueryException, IOException;
}
