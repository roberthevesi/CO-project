package bench.benchmarkco;

public class PiSpigot {
    public  int digits_requested;
    public  int[] digits;
    public StringBuilder predigits = new StringBuilder();

    public String piString = "";

    // Max value such that digits.length <= INT_MAX.
    //   ceil(((2**31-1) - 1) * 3 / 10)
    public  static final int MAX_DIGITS_REQUESTED = 644245094;


    public static void printHelp() {
        System.err.println("\n  PiSpigot [number of digits requested]\n");
    }


    public static void main(String args[]) {
        PiSpigot spigot = new PiSpigot();
        if (!spigot.parseArgs(args)) return;
        spigot.run();
    }


    // Get the number of digits requested from the command line arguments.
    public boolean parseArgs(String args[]) {
        if (args.length != 1) {
            printHelp();
            return false;
        }

        try {
            digits_requested = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            printHelp();
            return false;
        }

        if (digits_requested <= 0) {
            System.err.println("Digit count must be positive.");
            return false;
        }

        if (digits_requested > MAX_DIGITS_REQUESTED) {
            System.err.println("Maximum digit count is " + MAX_DIGITS_REQUESTED);
            return false;
        }

        return true;
    }


    // Allocate digits[]
    public boolean init() {
        int array_size_needed = digits_requested * 10 / 3 + 1;
        digits = new int[array_size_needed];
        if (digits == null) {
            System.err.printf("Failed to allocate " + (array_size_needed*4)
                    + " bytes.");
            return false;
        }

        // fill each digit with a 2
        for (int i=0; i < digits.length; i++)
            digits[i] = 2;

        return true;
    }


    // Produce digits
    void run() {
        if (!init()) return;

        for (int iter = 0; iter < digits_requested; iter++) {

            // Work backwards through the array, multiplying each digit by 10,
            // carrying the excess and leaving the remainder.
            int carry = 0;
            for (int i=digits.length-1; i > 0; i--) {
                int numerator = i;
                int denomenator = i * 2 + 1;
                int tmp = digits[i] * 10 + carry;
                digits[i] = tmp % denomenator;
                carry = tmp / denomenator * numerator;
            }

            // process the last digit
            int tmp = digits[0] * 10 + carry;
            digits[0] = tmp % 10;
            int digit = tmp / 10;

            // implement buffering and overflow
            if (digit < 9) {
                flushDigits();
                // print a decimal after the leading "3"
                if (iter == 1)
                    piString += "."; // System.out.print(".");
                addDigit(digit);
            } else if (digit == 9) {
                addDigit(digit);
            } else {
                overflowDigits();
                flushDigits();
                addDigit(0);
            }
            // System.out.flush();
        }
        flushDigits();
        System.out.println();
    }


    // write the buffered digits
    void flushDigits() {
        ///System.out.append(predigits);
//        System.out.println(predigits);
        piString += predigits;
        predigits.setLength(0);
    }


    // given an integer 0..9, buffer a digit '0' .. '9'
    void addDigit(int digit) {
        predigits.append((char)('0' + digit));
    }


    // add one to each digit, rolling over from from 9 to 0
    void overflowDigits() {
        for (int i=0; i < predigits.length(); i++) {
            char digit = predigits.charAt(i);
            // This could be implemented with a modulo, but compared to the main
            // loop this code is too quick to measure.
            if (digit == '9') {
                predigits.setCharAt(i, '0');
            } else {
                predigits.setCharAt(i, (char)(digit + 1));
            }
        }
    }

}