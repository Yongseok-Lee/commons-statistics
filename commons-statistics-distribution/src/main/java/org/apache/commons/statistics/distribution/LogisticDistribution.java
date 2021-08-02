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
 * Implementation of the <a href="http://en.wikipedia.org/wiki/Logistic_distribution">Logistic distribution</a>.
 */
public class LogisticDistribution extends AbstractContinuousDistribution {
    /** Support lower bound. */
    private static final double SUPPORT_LO = Double.NEGATIVE_INFINITY;
    /** Support upper bound. */
    private static final double SUPPORT_HI = Double.POSITIVE_INFINITY;
    /** &pi;<sup>2</sup>/3. */
    private static final double PI_SQUARED_OVER_THREE = Math.PI * Math.PI / 3;
    /** Location parameter. */
    private final double mu;
    /** Scale parameter. */
    private final double scale;
    /** Inverse of "scale". */
    private final double oneOverScale;

    /**
     * Creates a distribution.
     *
     * @param mu Location parameter.
     * @param scale Scale parameter (must be positive).
     * @throws IllegalArgumentException if {@code scale <= 0}.
     */
    public LogisticDistribution(double mu,
                                double scale) {
        if (scale <= 0) {
            throw new DistributionException(DistributionException.NOT_STRICTLY_POSITIVE,
                                            scale);
        }

        this.mu = mu;
        this.scale = scale;
        this.oneOverScale = 1 / scale;
    }

    /**
     * Gets the location parameter.
     *
     * @return the location parameter.
     */
    public double getLocation() {
        return mu;
    }

    /**
     * Gets the scale parameter.
     *
     * @return the scale parameter.
     */
    public double getScale() {
        return scale;
    }

    /** {@inheritDoc} */
    @Override
    public double density(double x) {
        if (x <= SUPPORT_LO ||
            x >= SUPPORT_HI) {
            return 0;
        }

        final double z = oneOverScale * (x - mu);
        final double v = Math.exp(-z);
        return oneOverScale * v / ((1 + v) * (1 + v));
    }

    /** {@inheritDoc} */
    @Override
    public double logDensity(double x) {
        if (x <= SUPPORT_LO ||
            x >= SUPPORT_HI) {
            return Double.NEGATIVE_INFINITY;
        }

        final double z = oneOverScale * (x - mu);
        final double v = Math.exp(-z);
        return -Math.log(scale) - z - 2 * Math.log(1 + v);
    }

    /** {@inheritDoc} */
    @Override
    public double cumulativeProbability(double x) {
        final double z = oneOverScale * (x - mu);
        return 1 / (1 + Math.exp(-z));
    }

    /** {@inheritDoc} */
    @Override
    public double survivalProbability(double x) {
        final double z = oneOverScale * (x - mu);
        return 1 / (1 + Math.exp(z));
    }

    /** {@inheritDoc} */
    @Override
    public double inverseCumulativeProbability(double p) {
        if (p < 0 ||
            p > 1) {
            throw new DistributionException(DistributionException.INVALID_PROBABILITY, p);
        } else if (p == 0) {
            return SUPPORT_LO;
        } else if (p == 1) {
            return SUPPORT_HI;
        } else {
            return scale * Math.log(p / (1 - p)) + mu;
        }
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
     * <p>For scale parameter {@code s}, the variance is {@code s^2 * pi^2 / 3}.
     */
    @Override
    public double getVariance() {
        return scale * scale * PI_SQUARED_OVER_THREE;
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
        return SUPPORT_LO;
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
        return SUPPORT_HI;
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
