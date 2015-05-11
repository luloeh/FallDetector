package de.dfki.falldetector;

/**
 * decision tree classifier autogenerated by Weka
 * some types have been adjusted for use with this application
 */
class WekaClassifier {

    public static double classify(Float[] i) {
        return WekaClassifier.N76e17c9834(i);
    }

    static double N76e17c9834(Float[] i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if (i[7] <= 20.0) {
            p = WekaClassifier.N6328bb1d35(i);
        } else if (i[7] > 20.0) {
            p = WekaClassifier.N6dde775b36(i);
        }
        return p;
    }

    static double N6328bb1d35(Float[] i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 2;
        } else if (i[10] <= 27.544741) {
            p = 2;
        } else if (i[10] > 27.544741) {
            p = 3;
        }
        return p;
    }

    static double N6dde775b36(Float[] i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 6;
        } else if (i[10] <= 28.436434) {
            p = WekaClassifier.N6e5ede5337(i);
        } else if (i[10] > 28.436434) {
            p = WekaClassifier.N20f243e943(i);
        }
        return p;
    }

    static double N6e5ede5337(Float[] i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 6;
        } else if (i[9] <= 13.457101) {
            p = WekaClassifier.N13a66abc38(i);
        } else if (i[9] > 13.457101) {
            p = WekaClassifier.N136d96e942(i);
        }
        return p;
    }

    static double N13a66abc38(Float[] i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 6;
        } else if (i[11] <= 23.999838) {
            p = WekaClassifier.N35f0db8639(i);
        } else if (i[11] > 23.999838) {
            p = 0;
        }
        return p;
    }

    static double N35f0db8639(Float[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 6;
        } else if (i[6] <= 13.0) {
            p = WekaClassifier.N2b0a16cc40(i);
        } else if (i[6] > 13.0) {
            p = 5;
        }
        return p;
    }

    static double N2b0a16cc40(Float[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 6;
        } else if (i[5] <= 2.0) {
            p = 6;
        } else if (i[5] > 2.0) {
            p = WekaClassifier.N333ee9b841(i);
        }
        return p;
    }

    static double N333ee9b841(Float[] i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 6;
        } else if (i[8] <= 56.0) {
            p = 6;
        } else if (i[8] > 56.0) {
            p = 4;
        }
        return p;
    }

    static double N136d96e942(Float[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (i[0] <= 2.0) {
            p = 2;
        } else if (i[0] > 2.0) {
            p = 6;
        }
        return p;
    }

    static double N20f243e943(Float[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (i[4] <= 4.0) {
            p = WekaClassifier.N5bb58d4d44(i);
        } else if (i[4] > 4.0) {
            p = WekaClassifier.N721ca43950(i);
        }
        return p;
    }

    static double N5bb58d4d44(Float[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (i[6] <= 9.0) {
            p = WekaClassifier.N1f02ccaa45(i);
        } else if (i[6] > 9.0) {
            p = WekaClassifier.N25d0017c48(i);
        }
        return p;
    }

    static double N1f02ccaa45(Float[] i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 0;
        } else if (i[8] <= 86.0) {
            p = WekaClassifier.N6027cb7d46(i);
        } else if (i[8] > 86.0) {
            p = 2;
        }
        return p;
    }

    static double N6027cb7d46(Float[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (i[5] <= 4.0) {
            p = 0;
        } else if (i[5] > 4.0) {
            p = WekaClassifier.N5278319b47(i);
        }
        return p;
    }

    static double N5278319b47(Float[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (i[3] <= 0.0) {
            p = 0;
        } else if (i[3] > 0.0) {
            p = 2;
        }
        return p;
    }

    static double N25d0017c48(Float[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (i[1] <= 0.0) {
            p = WekaClassifier.N2cbefef049(i);
        } else if (i[1] > 0.0) {
            p = 0;
        }
        return p;
    }

    static double N2cbefef049(Float[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 4;
        } else if (i[2] <= 0.0) {
            p = 4;
        } else if (i[2] > 0.0) {
            p = 0;
        }
        return p;
    }

    static double N721ca43950(Float[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 4;
        } else if (i[3] <= 0.0) {
            p = 4;
        } else if (i[3] > 0.0) {
            p = 3;
        }
        return p;
    }
}
