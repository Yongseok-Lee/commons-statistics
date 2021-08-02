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

/**
 * This class implements the Laplace distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Laplace_distribution">Laplace distribution (Wikipedia)</a>
 */
public class LaplaceDistribution extends AbstractContinuousDistribution {
    /** The location parameter. */
    private final double mu;
    /** The scale parameter. */
    private final double beta;
    /** log(2 * beta). */
    private final double log2beta;

    /**
     * Creates a distribution.
     *
     * @param mu location parameter
     * @param beta scale parameter (must be positive)
     * @throws IllegalArgumentException if {@code beta <= 0}
     */
    public LaplaceDistribution(double mu,
                               double beta) {
        if (beta <= 0) {
            throw new DistributionException(DistributionException.NOT_STRICTLY_POSITIVE, beta);
        }

        this.mu = mu;
        this.beta = beta;
        log2beta = Math.log(2.0 * beta);
    }

    /**
     * Access the location parameter, {@code mu}.
     *
     * @return the location parameter.
     */
    public double getLocation() {
        return mu;
    }

    /**
     * Access the scale parameter, {@code beta}.
     *
     * @return the scale parameter.
     */
    public double getScale() {
        return beta;
    }

    /** {@inheritDoc} */
    @Override
    public double density(double x) {
        return Math.exp(-Math.abs(x - mu) / beta) / (2.0 * beta);
    }

    /** {@inheritDoc} */
    @Override
    public double logDensity(double x) {
        return -Math.abs(x - mu) / beta - log2beta;
    }

    /** {@inheritDoc} */
    @Override
    public double cumulativeProbability(double x) {
        if (x <= mu) {
            return Math.exp((x - mu) / beta) / 2.0;
        }
        return 1.0 - Math.exp((mu - x) / beta) / 2.0;
    }

    /** {@inheritDoc} */
    @Override
    public double survivalProbability(double x) {
        if (x <= mu) {
            return 1.0 - Math.exp((x - mu) / beta) / 2.0;
        }
        return Math.exp((mu - x) / beta) / 2.0;
    }

    /** {@inheritDoc} */
    @Override
    public double inverseCumulativeProbability(double p) {
        if (p < 0 ||
            p > 1) {
            throw new DistributionException(DistributionException.INVALID_PROBABILITY, p);
        } else if (p == 0) {
            return Double.NEGATIVE_INFINITY;
        } else if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        final double x = (p > 0.5) ? -Math.log(2.0 - 2.0 * p) : Math.log(2.0 * p);
        return mu + beta * x;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The mean is equal to the {@link #getLocation() location}.
     */
    @Override
    public double getMean() {
        return getLocation();
    }

    /**
     * {@inheritDoc}
     *
     * <p>The variance is {@code 2 * beta^2}.
     */
    @Override
    public double getVariance() {
        return 2.0 * beta * beta;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The lower bound of the support is always negative infinity
     * no matter the parameters.
     *
     * @return lower bound of the support (always
     * {@code Double.NEGATIVE_INFINITY})
     */
    @Override
    public double getSupportLowerBound() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The upper bound of the support is always positive infinity
     * no matter the parameters.
     *
     * @return upper bound of the support (always
     * {@code Double.POSITIVE_INFINITY})
     */
    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The support of this distribution is connected.
     *
     * @return {@code true}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }
}
