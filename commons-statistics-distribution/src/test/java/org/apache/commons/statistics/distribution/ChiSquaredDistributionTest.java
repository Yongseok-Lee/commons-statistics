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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test cases for {@link ChiSquaredDistribution}.
 *
 * @see ContinuousDistributionAbstractTest
 */
class ChiSquaredDistributionTest extends ContinuousDistributionAbstractTest {

    //---------------------- Override tolerance --------------------------------

    @BeforeEach
    void customSetUp() {
        setTolerance(1e-9);
    }

    //-------------- Implementations for abstract methods ----------------------

    @Override
    public ChiSquaredDistribution makeDistribution() {
        return new ChiSquaredDistribution(5.0);
    }

    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 2.9.2
        return new double[] {0.210212602629, 0.554298076728, 0.831211613487, 1.14547622606, 1.61030798696,
                             20.5150056524, 15.0862724694, 12.8325019940, 11.0704976935, 9.23635689978};
    }

    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001, 0.01, 0.025, 0.05, 0.1, 0.999, 0.990, 0.975, 0.950, 0.900};
    }

    @Override
    public double[] makeInverseCumulativeTestPoints() {
        return new double[] {0, 0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                             0.990d, 0.975d, 0.950d, 0.900d, 1};
    }

    @Override
    public double[] makeInverseCumulativeTestValues() {
        return new double[] {0, 0.210212602629, 0.554298076728, 0.831211613487, 1.14547622606, 1.61030798696,
                             20.5150056524, 15.0862724694, 12.8325019940, 11.0704976935, 9.23635689978,
                             Double.POSITIVE_INFINITY};
    }

    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0.0115379817652, 0.0415948507811, 0.0665060119842, 0.0919455953114, 0.121472591024,
                             0.000433630076361, 0.00412780610309, 0.00999340341045, 0.0193246438937, 0.0368460089216};
    }

    @Override
    public double[] makeCumulativePrecisionTestPoints() {
        return new double[] {1e-7, 4e-7, 9e-8};
    }

    @Override
    public double[] makeCumulativePrecisionTestValues() {
        // These were created using WolframAlpha
        return new double[] {1.6820882879388572e-19, 5.382681944688393e-18, 1.292572946953654e-19};
    }

    @Override
    public double[] makeSurvivalPrecisionTestPoints() {
        return new double[] {93, 97.3};
    }

    @Override
    public double[] makeSurvivalPrecisionTestValues() {
        // These were created using WolframAlpha
        return new double[] {1.5731947657596637e-18, 1.9583114656146269e-19};
    }

    //-------------------- Additional test cases -------------------------------

    @Test
    void testSmallDf() {
        setDistribution(new ChiSquaredDistribution(0.1d));
        setTolerance(1E-4);
        // quantiles computed using R version 1.8.1 (linux version)
        setCumulativeTestPoints(new double[] {1.168926E-60, 1.168926E-40, 1.063132E-32,
                                              1.144775E-26, 1.168926E-20, 5.472917, 2.175255, 1.13438,
                                              0.5318646, 0.1526342});
        setCumulativeTestValues(makeCumulativeTestValues());
        setInverseCumulativeTestValues(getCumulativeTestPoints());
        setInverseCumulativeTestPoints(getCumulativeTestValues());
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.5, 1, 2})
    void testParameterAccessors(double df) {
        final ChiSquaredDistribution dist = new ChiSquaredDistribution(df);
        Assertions.assertEquals(df, dist.getDegreesOfFreedom());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -0.1})
    void testConstructorPrecondition(double df) {
        Assertions.assertThrows(DistributionException.class, () -> new ChiSquaredDistribution(df));
    }

    @Test
    void testMoments() {
        final double tol = 1e-9;
        ChiSquaredDistribution dist;

        dist = new ChiSquaredDistribution(1500);
        Assertions.assertEquals(1500, dist.getMean(), tol);
        Assertions.assertEquals(3000, dist.getVariance(), tol);

        dist = new ChiSquaredDistribution(1.12);
        Assertions.assertEquals(1.12, dist.getMean(), tol);
        Assertions.assertEquals(2.24, dist.getVariance(), tol);
    }

    @Test
    void testDensity() {
        final double[] x = new double[]{-0.1, 1e-6, 0.5, 1, 2, 5};
        //R 2.5: print(dchisq(x, df=1), digits=10)
        checkDensity(1, x, new double[]{0.00000000000, 398.94208093034, 0.43939128947, 0.24197072452, 0.10377687436, 0.01464498256});
        //R 2.5: print(dchisq(x, df=0.1), digits=10)
        checkDensity(0.1, x, new double[]{0.000000000e+00, 2.486453997e+04, 7.464238732e-02, 3.009077718e-02, 9.447299159e-03, 8.827199396e-04});
        //R 2.5: print(dchisq(x, df=2), digits=10)
        checkDensity(2, x, new double[]{0.00000000000, 0.49999975000, 0.38940039154, 0.30326532986, 0.18393972059, 0.04104249931});
        //R 2.5: print(dchisq(x, df=10), digits=10)
        checkDensity(10, x, new double[]{0.000000000e+00, 1.302082682e-27, 6.337896998e-05, 7.897534632e-04, 7.664155024e-03, 6.680094289e-02});
    }

    private void checkDensity(double df, double[] x, double[] expected) {
        final ChiSquaredDistribution dist = new ChiSquaredDistribution(df);
        for (int i = 0; i < x.length; i++) {
            Assertions.assertEquals(expected[i], dist.density(x[i]), 1e-5);
        }
    }
}
