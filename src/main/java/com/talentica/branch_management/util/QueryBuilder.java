package com.talentica.branch_management.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Slf4j
public class QueryBuilder {

    private static final String EQUAL = "eq";
    private static final String REGEX = "regex";
    private static final String PIPE = "\\|";
    private static final String COLON = ":";
    private static final String INVALID_FILTER_ERROR = "Invalid filter is present";
    private static final String INVALID_CRITERIA_ERROR = "Invalid criteria is present";

    /**
     *
     * @param filterAnd And filter (as a single parameter)
     * @param filterOr Or Filter (as a single parameter)
     * @return Query built with Criteria built from filterAnd and filterOr
     */
    public static Query buildQuery(String filterAnd, String filterOr){
        String[] filterAndSeparated = filterAnd.split(PIPE);
        String[] filterOrSeparated = filterOr.split(PIPE);

        Criteria finalCriteria = new Criteria();
        for (String filter : filterAndSeparated){
            Criteria criteria = buildCriteria(filter.split(COLON));
            finalCriteria.andOperator(criteria);
        }

        for (String filter : filterOrSeparated){
            Criteria criteria = buildCriteria(filter.split(COLON));
            finalCriteria.orOperator(criteria);
        }

        return buildQuery(finalCriteria);
    }

    private static Query buildQuery(Criteria finalCriteria) {
        Query query = new Query();
        query.addCriteria(finalCriteria);
        return query;
    }

    private static Criteria buildCriteria(String[] filter){
        if(filter.length<2){
            throw new IllegalArgumentException(INVALID_FILTER_ERROR);
        }

        if(StringUtils.isAnyEmpty(filter)){
            throw new IllegalArgumentException(INVALID_FILTER_ERROR);
        }

        if(EQUAL.equalsIgnoreCase(filter[1])){
            return Criteria.where(filter[0]).is(filter[2]);
        }else if(REGEX.equalsIgnoreCase(filter[1])){
            return Criteria.where(filter[0]).regex(filter[2]);
        }

        throw new IllegalArgumentException(INVALID_CRITERIA_ERROR);
    }
}
