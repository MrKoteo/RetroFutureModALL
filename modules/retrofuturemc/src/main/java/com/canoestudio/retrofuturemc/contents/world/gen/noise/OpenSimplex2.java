package com.canoestudio.retrofuturemc.contents.world.gen.noise;

/**
 * Minimal 3D OpenSimplex2 evaluator, adapted from K.jpg's faster variant.
 */
public final class OpenSimplex2 {
    private static final long PRIME_X = 0x5205402B9270C86FL;
    private static final long PRIME_Y = 0x598CD327003817B5L;
    private static final long PRIME_Z = 0x5BCC226E9FA0BACBL;
    private static final long HASH_MULTIPLIER = 0x53A3F72DEEC546F5L;
    private static final long SEED_FLIP_3D = -0x52D547B2E96ED629L;

    private static final double ROOT3OVER3 = 0.577350269189626D;
    private static final double ROTATE_3D_ORTHOGONALIZER = -0.21132486540518713D;
    private static final int N_GRADS_3D_EXPONENT = 8;
    private static final int N_GRADS_3D = 1 << N_GRADS_3D_EXPONENT;
    private static final double NORMALIZER_3D = 0.07969837668935331D;
    private static final float RSQUARED_3D = 0.6F;
    private static final float[] GRADIENTS_3D = new float[N_GRADS_3D * 4];

    private OpenSimplex2() {
    }

    public static double fractal3_ImproveXZ(long seed, double x, double y, double z, int octaves, double frequency, double lacunarity, double gain) {
        double value = 0.0D;
        double amplitude = 1.0D;
        double amplitudeSum = 0.0D;
        double sampleX = x * frequency;
        double sampleY = y * frequency;
        double sampleZ = z * frequency;

        for (int i = 0; i < octaves; i++) {
            value += noise3_ImproveXZ(seed + i * 0x9E3779B97F4A7C15L, sampleX, sampleY, sampleZ) * amplitude;
            amplitudeSum += amplitude;
            sampleX *= lacunarity;
            sampleY *= lacunarity;
            sampleZ *= lacunarity;
            amplitude *= gain;
        }

        return amplitudeSum == 0.0D ? 0.0D : value / amplitudeSum;
    }

    public static float noise3_ImproveXZ(long seed, double x, double y, double z) {
        double xz = x + z;
        double s2 = xz * ROTATE_3D_ORTHOGONALIZER;
        double yy = y * ROOT3OVER3;
        double xr = x + s2 + yy;
        double zr = z + s2 + yy;
        double yr = xz * -ROOT3OVER3 + yy;

        return noise3_UnrotatedBase(seed, xr, yr, zr);
    }

    private static float noise3_UnrotatedBase(long seed, double xr, double yr, double zr) {
        int xrb = fastRound(xr);
        int yrb = fastRound(yr);
        int zrb = fastRound(zr);
        float xri = (float)(xr - xrb);
        float yri = (float)(yr - yrb);
        float zri = (float)(zr - zrb);

        int xNSign = (int)(-1.0F - xri) | 1;
        int yNSign = (int)(-1.0F - yri) | 1;
        int zNSign = (int)(-1.0F - zri) | 1;
        float ax0 = xNSign * -xri;
        float ay0 = yNSign * -yri;
        float az0 = zNSign * -zri;

        long xrbp = xrb * PRIME_X;
        long yrbp = yrb * PRIME_Y;
        long zrbp = zrb * PRIME_Z;
        float value = 0.0F;
        float a = (RSQUARED_3D - xri * xri) - (yri * yri + zri * zri);

        for (int lattice = 0; ; lattice++) {
            if (a > 0.0F) {
                value += (a * a) * (a * a) * grad(seed, xrbp, yrbp, zrbp, xri, yri, zri);
            }

            if (ax0 >= ay0 && ax0 >= az0) {
                float b = a + ax0 + ax0;
                if (b > 1.0F) {
                    b -= 1.0F;
                    value += (b * b) * (b * b) * grad(seed, xrbp - xNSign * PRIME_X, yrbp, zrbp, xri + xNSign, yri, zri);
                }
            } else if (ay0 > ax0 && ay0 >= az0) {
                float b = a + ay0 + ay0;
                if (b > 1.0F) {
                    b -= 1.0F;
                    value += (b * b) * (b * b) * grad(seed, xrbp, yrbp - yNSign * PRIME_Y, zrbp, xri, yri + yNSign, zri);
                }
            } else {
                float b = a + az0 + az0;
                if (b > 1.0F) {
                    b -= 1.0F;
                    value += (b * b) * (b * b) * grad(seed, xrbp, yrbp, zrbp - zNSign * PRIME_Z, xri, yri, zri + zNSign);
                }
            }

            if (lattice == 1) {
                break;
            }

            ax0 = 0.5F - ax0;
            ay0 = 0.5F - ay0;
            az0 = 0.5F - az0;
            xri = xNSign * ax0;
            yri = yNSign * ay0;
            zri = zNSign * az0;
            a += (0.75F - ax0) - (ay0 + az0);
            xrbp += (xNSign >> 1) & PRIME_X;
            yrbp += (yNSign >> 1) & PRIME_Y;
            zrbp += (zNSign >> 1) & PRIME_Z;
            xNSign = -xNSign;
            yNSign = -yNSign;
            zNSign = -zNSign;
            seed ^= SEED_FLIP_3D;
        }

        return value;
    }

    private static float grad(long seed, long xrvp, long yrvp, long zrvp, float dx, float dy, float dz) {
        long hash = (seed ^ xrvp) ^ (yrvp ^ zrvp);
        hash *= HASH_MULTIPLIER;
        hash ^= hash >> (64 - N_GRADS_3D_EXPONENT + 2);
        int gi = (int)hash & ((N_GRADS_3D - 1) << 2);
        return GRADIENTS_3D[gi] * dx + GRADIENTS_3D[gi | 1] * dy + GRADIENTS_3D[gi | 2] * dz;
    }

    private static int fastRound(double x) {
        return x < 0.0D ? (int)(x - 0.5D) : (int)(x + 0.5D);
    }

    static {
        float[] grad3 = {
                2.22474487139F, 2.22474487139F, -1.0F, 0.0F,
                2.22474487139F, 2.22474487139F, 1.0F, 0.0F,
                3.0862664687972017F, 1.1721513422464978F, 0.0F, 0.0F,
                1.1721513422464978F, 3.0862664687972017F, 0.0F, 0.0F,
                -2.22474487139F, 2.22474487139F, -1.0F, 0.0F,
                -2.22474487139F, 2.22474487139F, 1.0F, 0.0F,
                -1.1721513422464978F, 3.0862664687972017F, 0.0F, 0.0F,
                -3.0862664687972017F, 1.1721513422464978F, 0.0F, 0.0F,
                -1.0F, -2.22474487139F, -2.22474487139F, 0.0F,
                1.0F, -2.22474487139F, -2.22474487139F, 0.0F,
                0.0F, -3.0862664687972017F, -1.1721513422464978F, 0.0F,
                0.0F, -1.1721513422464978F, -3.0862664687972017F, 0.0F,
                -1.0F, -2.22474487139F, 2.22474487139F, 0.0F,
                1.0F, -2.22474487139F, 2.22474487139F, 0.0F,
                0.0F, -1.1721513422464978F, 3.0862664687972017F, 0.0F,
                0.0F, -3.0862664687972017F, 1.1721513422464978F, 0.0F,
                -2.22474487139F, -2.22474487139F, -1.0F, 0.0F,
                -2.22474487139F, -2.22474487139F, 1.0F, 0.0F,
                -3.0862664687972017F, -1.1721513422464978F, 0.0F, 0.0F,
                -1.1721513422464978F, -3.0862664687972017F, 0.0F, 0.0F,
                -2.22474487139F, -1.0F, -2.22474487139F, 0.0F,
                -2.22474487139F, 1.0F, -2.22474487139F, 0.0F,
                -1.1721513422464978F, 0.0F, -3.0862664687972017F, 0.0F,
                -3.0862664687972017F, 0.0F, -1.1721513422464978F, 0.0F,
                -2.22474487139F, -1.0F, 2.22474487139F, 0.0F,
                -2.22474487139F, 1.0F, 2.22474487139F, 0.0F,
                -3.0862664687972017F, 0.0F, 1.1721513422464978F, 0.0F,
                -1.1721513422464978F, 0.0F, 3.0862664687972017F, 0.0F,
                -1.0F, 2.22474487139F, -2.22474487139F, 0.0F,
                1.0F, 2.22474487139F, -2.22474487139F, 0.0F,
                0.0F, 1.1721513422464978F, -3.0862664687972017F, 0.0F,
                0.0F, 3.0862664687972017F, -1.1721513422464978F, 0.0F,
                -1.0F, 2.22474487139F, 2.22474487139F, 0.0F,
                1.0F, 2.22474487139F, 2.22474487139F, 0.0F,
                0.0F, 3.0862664687972017F, 1.1721513422464978F, 0.0F,
                0.0F, 1.1721513422464978F, 3.0862664687972017F, 0.0F,
                2.22474487139F, -2.22474487139F, -1.0F, 0.0F,
                2.22474487139F, -2.22474487139F, 1.0F, 0.0F,
                1.1721513422464978F, -3.0862664687972017F, 0.0F, 0.0F,
                3.0862664687972017F, -1.1721513422464978F, 0.0F, 0.0F,
                2.22474487139F, -1.0F, -2.22474487139F, 0.0F,
                2.22474487139F, 1.0F, -2.22474487139F, 0.0F,
                3.0862664687972017F, 0.0F, -1.1721513422464978F, 0.0F,
                1.1721513422464978F, 0.0F, -3.0862664687972017F, 0.0F,
                2.22474487139F, -1.0F, 2.22474487139F, 0.0F,
                2.22474487139F, 1.0F, 2.22474487139F, 0.0F,
                1.1721513422464978F, 0.0F, 3.0862664687972017F, 0.0F,
                3.0862664687972017F, 0.0F, 1.1721513422464978F, 0.0F
        };

        for (int i = 0; i < grad3.length; i++) {
            grad3[i] = (float)(grad3[i] / NORMALIZER_3D);
        }

        for (int i = 0, j = 0; i < GRADIENTS_3D.length; i++, j++) {
            if (j == grad3.length) {
                j = 0;
            }
            GRADIENTS_3D[i] = grad3[j];
        }
    }
}
