public class Alice {

    public static void main(String[] args) {

        Data fileZero = new Data(0, false);
        Data fileOne = new Data(1, false);

        fileZero.encrypt();
        System.out.println("***" + ((0 == fileZero.decrypt()) ? "Success" : "Fail") + "***");

        fileOne.encrypt();
        System.out.println("***" + ((1 == fileOne.decrypt()) ? "Success" : "Fail") + "***");







    }
}
