/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.statistics.distribution;

import org.apache.commons.numbers.gamma.RegularizedBeta;

/**
 * Implementation of the binomial distribution.
 *
 * <p>The probability mass function of \( X \) is:
 *
 * <p>\[ f(k; n, p) = \binom{n}{k} p^k (1-p)^{n-k} \]
 *
 * <p>for \( n \in \{0, 1, 2, \dots\} \) the number of trials,
 * \( p \in [0, 1] \) the probability of success,
 * \( k \in \{0, 1, \dots, n\} \) the number of successes, and
 *
 * <p>\[ \binom{n}{k} = \frac{n!}{k! \, (n-k)!} \]
 *
 * <p>is the binomial coefficient.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Binomial_distribution">Binomial distribution (Wikipedia)</a>
 * @see <a href="https://mathworld.wolfram.com/BinomialDistribution.html">Binomial distribution (MathWorld)</a>
 */
public final class BinomialDistribution extends AbstractDiscreteDistribution {
    /** 1/2. */
    private static final float HALF = 0.5f;

    /** The number of trials. */
    private final int numberOfTrials;
    /** The probability of success. */
    private final double probabilityOfSuccess;
    /** Cached value for pmf(x=0). */
    private final double pmf0;
    /** Cached value for pmf(x=n). */
    private final double pmfn;

    /**
     * @param trials Number of trials.
     * @param p Probability of success.
     */
    private BinomialDistribution(int trials,
                                 double p) {
        probabilityOfSuccess = p;
        numberOfTrials = trials;
        // Special pmf cases where the power function is more accurate:
        //   (n choose k) == 1 for k=0, k=n
        //   pmf = p^k (1-p)^(n-k)
        // Note: This handles the edge case of n=0: pmf(k=0) = 1, else 0
        if (probabilityOfSuccess >= HALF) {
            pmf0 = Math.pow(1 - probabilityOfSuccess, numberOfTrials);
        } else {
            pmf0 = Math.exp(numberOfTrials * Math.log1p(-probabilityOfSuccess));
        }
        pmfn = Math.pow(probabilityOfSuccess, numberOfTrials);
    }

    /**
     * Creates a binomial distribution.
     *
     * @param trials Number of trials.
     * @param p Probability of success.
     * @return the distribution
     * @throws IllegalArgumentException if {@code trials < 0}, or if {@code p < 0}
     * or {@code p > 1}.
     */
    public static BinomialDistribution of(int trials,
                                          double p) {
        if (trials < 0) {
            throw new DistributionException(DistributionException.NEGATIVE,
                                            trials);
        }
        ArgumentUtils.checkProbability(p);
        // Avoid p = -0.0 to avoid returning -0.0 for some probability computations.
        return new BinomialDistribution(trials, Math.abs(p));
    }

    /**
     * Gets the number of trials parameter of this distribution.
     *
     * @return the number of trials.
     */
    public int getNumberOfTrials() {
        return numberOfTrials;
    }

    /**
     * Gets the probability of success parameter of this distribution.
     *
     * @return the probability of success.
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /** {@inheritDoc} */
    @Override
    public double probability(int x) {
        if (x < 0 || x > numberOfTrials) {
            return 0;
        } else if (x == 0) {
            return pmf0;
        } else if (x == numberOfTrials) {
            return pmfn;
        }
        return Math.exp(SaddlePointExpansionUtils.logBinomialProbability(x,
                        numberOfTrials, probabilityOfSuccess,
                        1.0 - probabilityOfSuccess));
    }

    /** {@inheritDoc} **/
    @Override
    public double logProbability(int x) {
        if (numberOfTrials == 0) {
            return (x == 0) ? 0.0 : Double.NEGATIVE_INFINITY;
        } else if (x < 0 || x > numberOfTrials) {
            return Double.NEGATIVE_INFINITY;
        }
        // Special cases for x=0, x=n
        // are handled in the saddle point expansion
        return SaddlePointExpansionUtils.logBinomialProbability(x,
                numberOfTrials, probabilityOfSuccess,
                1.0 - probabilityOfSuccess);
    }

    /** {@inheritDoc} */
    @Override
    public double cumulativeProbability(int x) {
        if (x < 0) {
            return 0.0;
        } else if (x > numberOfTrials) {
            return 1.0;
        } else if (x == 0) {
            return pmf0;
        }
        return RegularizedBeta.complement(probabilityOfSuccess,
                                          x + 1.0, (double) numberOfTrials - x);
    }

    /** {@inheritDoc} */
    @Override
    public double survivalProbability(int x) {
        if (x < 0) {
            return 1.0;
        } else if (x >= numberOfTrials) {
            return 0.0;
        } else if (x == numberOfTrials - 1) {
            return pmfn;
        }
        return RegularizedBeta.value(probabilityOfSuccess,
                                     x + 1.0, (double) numberOfTrials - x);
    }

    /**
     * {@inheritDoc}
     *
     * <p>For number of trials \( n \) and probability of success \( p \), the mean is \( np \).
     */
    @Override
    public double getMean() {
        return numberOfTrials * probabilityOfSuccess;
    }

    /**
     * {@inheritDoc}
     *
     * <p>For number of trials \( n \) and probability of success \( p \), the variance is \( np (1 - p) \).
     */
    @Override
    public double getVariance() {
        final double p = probabilityOfSuccess;
        return numberOfTrials * p * (1 - p);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The lower bound of the support is always 0 except for the probability
     * parameter {@code p = 1}.
     *
     * @return 0 or the number of trials.
     */
    @Override
    public int getSupportLowerBound() {
        return probabilityOfSuccess < 1.0 ? 0 : numberOfTrials;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The upper bound of the support is the number of trials except for the
     * probability parameter {@code p = 0}.
     *
     * @return number of trials or 0.
     */
    @Override
    public int getSupportUpperBound() {
        return probabilityOfSuccess > 0.0 ? numberOfTrials : 0;
    }

    /** {@inheritDoc} */
    @Override
    int getMedian() {
        // Overridden for the probability(int, int) method.
        // This is intentionally not a public method.
        // Can be floor or ceiling of np. For the probability in a range use the floor
        // as this only used for values >= median+1.
        return (int) (numberOfTrials * probabilityOfSuccess);
    }
}
