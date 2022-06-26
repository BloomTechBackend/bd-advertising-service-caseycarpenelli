package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.dao.TargetingGroupDao;
import com.amazon.ata.advertising.service.model.*;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;

import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import javax.inject.Inject;

/**
 * This class is responsible for picking the advertisement to be rendered.
 */
public class AdvertisementSelectionLogic implements Comparator<TargetingGroup> {

    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);

    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    private Random random = new Random();

    private TargetingGroupDao targetingGroupDao1;

    /**
     * Constructor for AdvertisementSelectionLogic.
     * @param contentDao Source of advertising content.
     * @param targetingGroupDao Source of targeting groups for each advertising content.
     */
    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
    }

    /**
     * Setter for Random class.
     * @param random generates random number used to select advertisements.
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Gets all of the content and metadata for the marketplace and determines which content can be shown.  Returns the
     * eligible content with the highest click through rate.  If no advertisement is available or eligible, returns an
     * EmptyGeneratedAdvertisement.
     *
     * @param customerId - the customer to generate a custom advertisement for
     * @param marketplaceId - the id of the marketplace the advertisement will be rendered on
     * @return an advertisement customized for the customer id provided, or an empty advertisement if one could
     *     not be generated.
     */
    public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        GeneratedAdvertisement generatedAdvertisement = new EmptyGeneratedAdvertisement();
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
            return new EmptyGeneratedAdvertisement();
        } else {
            final List<AdvertisementContent> contents = contentDao.get(marketplaceId);
//            if (StringUtils.isEmpty(customerId)) {
//                LOG.warn("the Id cannot be empty");
//            } else {
                //changed what the get took in because it was actually called contentid compared to my previous which was the customerid
                if (contents.isEmpty()) {
                    return new EmptyGeneratedAdvertisement();
                }
                List<TargetingGroup> content = new ArrayList<>();
                for (int i = 0; i < contents.size(); i++) {
//                    content = targetingGroupDao.get(contents.get(i).getContentId());
                    content.add(targetingGroupDao.get(contents.get(i).getContentId()).get(0));
                }

                for (int i = 0; i < contents.size(); i++) {
//                    content.add(targetingGroupDao.get(contents.get(i).getContentId()).get(i));
                    if (content.size() >= 1) {
                        if (content.get(i).getClickThroughRate() > .90) {
                            TargetingPredicateResult targetingPredicateResult = new TargetingEvaluator(new RequestContext(customerId, marketplaceId))
                                    .evaluate(content.get(i));
                            if (targetingPredicateResult.isTrue()) {
                                return new GeneratedAdvertisement(contents.get(i));
                            }
                        }
                    }
                }

//                    TargetingPredicateResult targetingPredicateResult = new TargetingEvaluator(new RequestContext(customerId, marketplaceId))
//                            .evaluate(finish.stream().findFirst().get());
//                    if (targetingPredicateResult.isTrue()) {
//                        AdvertisementContent trueAd = contents.get(random.nextInt(contents.size()));
//                        generatedAdvertisement = new GeneratedAdvertisement(trueAd);
//                    } else {
//                        generatedAdvertisement = new EmptyGeneratedAdvertisement();
//                    }

                if (CollectionUtils.isNotEmpty(contents)) {
                    AdvertisementContent randomAdvertisementContent = contents.get(random.nextInt(contents.size()));
                    generatedAdvertisement = new GeneratedAdvertisement(randomAdvertisementContent);
                }

            return generatedAdvertisement;
        }
    }

    @Override
    public int compare(TargetingGroup o1, TargetingGroup o2) {
        if (o1.getClickThroughRate() < o2.getClickThroughRate()) {
            return 0;
        }
        if (o1.getClickThroughRate() > o2.getClickThroughRate()) {
            return 1;
        }
        return 0;
    }

    @Override
    public Comparator<TargetingGroup> reversed() {
        return Comparator.super.reversed();
    }

    @Override
    public Comparator<TargetingGroup> thenComparing(Comparator<? super TargetingGroup> other) {
        return Comparator.super.thenComparing(other);
    }

    }
//generate ad activty calls this method.
// and then we get the content id and use the targeting dao to get a list of targeting groups. //PSSST its really customerID
// sort by click through rate
// then we use the targeting evaluator which takes in the targeting groups
//that returns a boolean if it is correct advertising
// then find first targeting group that is true
// if no eligible ad is found then return new empty generated
// otherwise return the ad with the highest click through rate.

// not sure how this is using the targeting group
//i know im missing something
//not seeing how the targetgroup and the advertising content connect

