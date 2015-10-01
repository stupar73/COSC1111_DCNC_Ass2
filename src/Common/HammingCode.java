package Common;

import java.util.Scanner;

/**
 * Utility class for Hamming code.
 * <br />
 * This class should not be instantiated.
 */
public final class HammingCode
{
    public static final double ERROR_CHANCE = 0.5;

    private HammingCode()
    {
        // Throws exception if instantiation of class is attempted
    }

    /**
     * Generate Hamming code for given {@code data}.
     *
     * @param data
     *            Data bits from which to calculate full Hamming code
     * @return Fill Hamming code for given {@code data}.
     */
    public static int[] generateHammingCode(int[] data)
    {
        int i = 0, parityCount = 0;

        // Calculate the number of parity bits required
        while(i < data.length)
        {
            /*
             * 2^(parity count) must equal the current position, where the
             * current position is (bits traversed + num parity bits + 1)
             * (+1 required for array index offset)
             */
            if(Math.pow(2, parityCount) == i + parityCount + 1)
            {
                parityCount++;
            }
            else
            {
                i++;
            }
        }

        int[] result = new int[data.length + parityCount];

        int j = 0, k = 0;
        for(i = 1; i <= result.length; i++)
        {
            if(Math.pow(2, j) == i)
            {
                // Found parity bit location, initialise to -1 to signify
                // it's not set
                result[i - 1] = -1;
                j++;
            }
            else
            {
                // Data bit location, copy from input data array
                result[k + j] = data[k++];
            }
        }

        // Set parity bits
        for(i = 0; i < parityCount; i++)
        {
            result[((int) Math.pow(2, i)) - 1] = computeParityBit(result, i);
        }

        return result;
    }

    /**
     * Calculates parity bit at bit position {@code power} for Hamming code
     * array {@code bits}.
     *
     * @param bits
     *            Full Hamming code array
     * @param power
     *            Bit position of parity bit being calculated
     * @return Parity bit for position {@code power} in Hamming code array\
     *         {@code bits}.
     */
    public static int computeParityBit(int[] bits, int power)
    {
        int parity = 0;

        for(int i = 0; i < bits.length; i++)
        {
            // Only looking at set bits
            if(bits[i] != -1)
            {
                /*
                 * Take current bit position index (array index + 1) and convert
                 * to binary
                 */
                int bitPos = i + 1;
                String bitPosBinary = Integer.toBinaryString(bitPos);

                /*
                 * If the bit at 2^(power) of bitPosBinary is 1, then it
                 * factors into the parity calculation
                 */
                int x = ((Integer.parseInt(bitPosBinary))
                        / ((int) Math.pow(10, power))) % 10;
                if(x == 1 && bits[i] == 1)
                {
                    parity ^= 1;
                }
            }
        }

        return parity;
    }

    /**
     * Checks the validity of a message encoded using Hamming code.
     *
     * @param message
     *            A message in an int array containing both data and parity bits
     * @return Array index of bit error, -1 if no error.
     */
    public static int checkHammingCode(int[] message)
    {
        // TODO

        return -1;
    }

    public static void main(String[] args)
    {
        /*
         * TODO This should all go in the client class, this was just for
         * testing
         */

        Scanner sc = new Scanner(System.in);

        // Message should be 11 bits (7 bits data, 4 bits parity)
        System.out.print("Enter data: ");

        String dataStr = sc.next();
        dataStr = dataStr.replaceAll(" ", "");

        int[] data = new int[dataStr.length()];
        for(int i = 0; i < dataStr.length(); i++)
        {
            data[i] = Character.getNumericValue(dataStr.charAt(i));
        }

        int[] message = generateHammingCode(data);

        System.out.print("Full message (with hamming parity): ");
        for(int i = 0; i < message.length; i++)
        {
            System.out.print(message[message.length - i - 1]);
        }
        System.out.println();

        // Random chance for a one-bit error
        if(Math.random() <= ERROR_CHANCE)
        {
            // Flip a random bit
            message[(int) (Math.random() * message.length)] ^= 1;
        }

        System.out.print("After random error chance: ");
        for(int i = 0; i < message.length; i++)
        {
            System.out.print(message[message.length - i - 1]);
        }
        System.out.println();

        sc.close();
    }
}
