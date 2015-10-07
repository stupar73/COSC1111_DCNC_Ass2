package Common;

import java.util.Scanner;

/**
 * Utility class for Hamming code.
 * <br />
 * This class should not be instantiated.
 */
public final class HammingCode
{
    private HammingCode()
    {
        // Throws exception if instantiation of class is attempted
    }

    /**
     * Generate Hamming code for given {@code data}.
     *
     * @param data
     *            data bits from which to calculate full Hamming code
     * @return full Hamming code for given {@code data}
     */
    public static int[] encode(int[] data)
    {
        int i = 0, parityCount = 0;

        // Calculate the number of parity bits required
        while (i < data.length)
        {
            /*
             * 2^(parity count) must equal the current position, where the
             * current position is (bits traversed + num parity bits + 1)
             * (+1 required for array index offset)
             */
            if (Math.pow(2, parityCount) == i + parityCount + 1)
            {
                parityCount++;
            }
            else
            {
                i++;
            }
        }

        int[] message = new int[data.length + parityCount];

        int numParityBits = 0, numDataBits = 0;
        for (i = 1; i <= message.length; i++)
        {
            if (Math.pow(2, numParityBits) == i)
            {
                // Found parity bit location, initialise to 0
                message[i - 1] = 0;
                numParityBits++;
            }
            else
            {
                // Data bit location, copy from input data array
                message[numDataBits + numParityBits] = data[numDataBits++];
            }
        }

        // Set parity bits
        for (i = 0; i < parityCount; i++)
        {
            message[((int) Math.pow(2, i)) - 1] = computeParityBit(message, i);
        }

        return message;
    }

    /**
     * Calculates parity bit at bit position {@code power} for Hamming code
     * array {@code bits}
     *
     * @param bits
     *            full Hamming code array
     * @param power
     *            bit position of parity bit being calculated
     * @return parity bit for position {@code power} in Hamming code array
     *         {@code bits}
     */
    private static int computeParityBit(int[] bits, int power)
    {
        int parity = 0;

        for (int i = 1; i <= bits.length; i++)
        {
            if ((i & (int) Math.pow(2, power)) > 0)
            {
                parity ^= bits[i - 1];
            }
        }

        return parity;
    }

    /**
     * TODO
     */
    private static int determineNumParityBits(int[] message)
    {
        int parityCount = 0;

        // Determine number of parity bits
        while (Math.pow(2, parityCount) <= message.length)
        {
            parityCount++;
        }

        return parityCount;
    }

    /**
     * Checks the validity of a message encoded using Hamming code.
     *
     * @param message
     *            the message in an int array containing both data and parity
     *            bits
     * @return
     *         <ul>
     *         <li>if no error: -1</li>
     *         <li>if single bit error: array index of error</li>
     *         </ul>
     */
    public static int checkHammingCode(int[] message)
    {
        int parityCount = determineNumParityBits(message);

        int[] parityBits = new int[parityCount];
        // Binary value of error location
        String syndrome = "";

        for (int power = 0; power < parityCount; power++)
        {
            for (int i = 0; i < message.length; i++)
            {
                // Convert current bit position index to binary
                int bitPos = i + 1;
                String bitPosBinary = Integer.toBinaryString(bitPos);

                /*
                 * If the bit at 2^(power) of bitPosBinary is 1, then it
                 * factors into the parity calculation
                 */
                int x = ((Integer.parseInt(bitPosBinary))
                        / ((int) Math.pow(10, power))) % 10;
                if (x == 1 && message[i] == 1)
                {
                    parityBits[power] = (parityBits[power] + 1) % 2;
                }
            }
            syndrome = parityBits[power] + syndrome;
        }

        int errorLocation = Integer.parseInt(syndrome, 2);

        if (errorLocation == 0)
        {
            // No error
            return -1;
        }

        // Else, single bit error - return array index of bit error
        return errorLocation - 1;
    }

    /**
     * Removes parity bits from {@code message} and return data bits
     *
     * @param message
     *            message in an int array containing both data and parity bits
     * @return data contained in {@code message} encoded using Hamming code
     */
    public static int[] removeParity(int[] message)
    {
        int parityCount = determineNumParityBits(message);
        int[] data = new int[message.length - parityCount];
        int dataBitsAdded = 0;

        for (int i = 0; i < message.length; i++)
        {
            if (!isPowerOfTwo(i + 1))
            {
                data[dataBitsAdded++] = message[i];
            }
        }

        return data;
    }

    /**
     * TODO
     * Credit:
     * https://stackoverflow.com/questions/600293/how-to-check-if-a-number-is-a-
     * power-of-2
     */
    private static Boolean isPowerOfTwo(int x)
    {
        return (x != 0) && ((x & (x - 1)) == 0);
    }

    /**
     * Print a message in an int array to {@code System.out}
     *
     * @param message
     *            message in an int array
     */
    public static void printMessage(int[] message)
    {
        for (int i = message.length - 1; i >= 0; i--)
        {
            System.out.print(message[i] + " ");
        }
        System.out.println();
    }

    /**
     * Print a message in an int array with a header to {@code System.out}
     *
     * @param message
     *            message in an int array
     */
    public static void printHeaderedMessage(int[] message)
    {
        String[] headerLine = new String[message.length];

        // Create header line
        int numParityBits = 0, numDataBits = 0;
        for (int i = 1; i <= message.length; i++)
        {
            if (Math.pow(2, numParityBits) == i)
            {
                // Parity bit location
                headerLine[i - 1] = "r" + (numParityBits++ + 1);
            }
            else
            {
                // Data bit location
                headerLine[i - 1] = "d" + (numDataBits++ + 1);
            }
        }

        // Print header line
        for (int i = headerLine.length - 1; i >= 0; i--)
        {
            System.out.print(headerLine[i] + "  ");
        }
        System.out.println();

        // Print data bits
        for (int i = message.length - 1; i >= 0; i--)
        {
            System.out.print(message[i] + "   ");
        }
        System.out.println();
    }
}
