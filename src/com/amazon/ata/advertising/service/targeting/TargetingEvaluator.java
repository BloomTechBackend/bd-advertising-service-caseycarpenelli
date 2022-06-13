package com.amazon.ata.advertising.service.targeting;

import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Evaluates TargetingPredicates for a given RequestContext.
 */
public class TargetingEvaluator {
    public static final boolean IMPLEMENTED_STREAMS = true;
    public static final boolean IMPLEMENTED_CONCURRENCY = false;
    final private RequestContext requestContext;

    /**
     * Creates an evaluator for targeting predicates.
     * @param requestContext Context that can be used to evaluate the predicates.
     */
    public TargetingEvaluator(RequestContext requestContext) {
        this.requestContext = requestContext;
    }


    /**
     * Evaluate a TargetingGroup to determine if all of its TargetingPredicates are TRUE or not for the given
     * RequestContext.
     * @param targetingGroup Targeting group for an advertisement, including TargetingPredicates.
     * @return TRUE if all of the TargetingPredicates evaluate to TRUE against the RequestContext, FALSE otherwise.
     */
    public TargetingPredicateResult evaluate(TargetingGroup targetingGroup) {
        boolean allTruePredicates = isAllTruePredicates(targetingGroup);

        return allTruePredicates ? TargetingPredicateResult.TRUE :
                                   TargetingPredicateResult.FALSE;

//        TargetingPredicateResult targetingPredicateResult = targetingGroup.getTargetingPredicates().stream()
//                .map(targetingPredicate -> evaluate(targetingGroup))
//                .findFirst().get().invert();



//        boolean trial = targetingGroup.getTargetingPredicates().stream()
//                .map(targetingPredicate -> evaluate(targetingGroup))
//                .map(TargetingPredicate::evaluate(requestContext))
//                .map(TargetingPredicate::predicates)
//                .allMatch(TargetingPredicateResult::isTrue);

    }

// we go through the targeting predicates wit the targeting group as the argument for evaluate
// then we determine if all are true.
// if they are we return TargetPrediateResult.True
// and the inverse if not.






















    private boolean isAllTruePredicates(TargetingGroup targetingGroup) {
        List<TargetingPredicate> targetingPredicates = targetingGroup.getTargetingPredicates();
        boolean allTruePredicates = true;
        for (TargetingPredicate predicate : targetingPredicates) {
            TargetingPredicateResult predicateResult = predicate.evaluate(requestContext);
            if (!predicateResult.isTrue()) {
                allTruePredicates = false;
                break;
            }
        }
        return allTruePredicates;
    }
}
