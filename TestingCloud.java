public class TestingCloud {

    public static void main(String[] args) {
        Cloud cloud = new Cloud();
        SimpleData f0 = new SimpleData(1, false);
        SimpleData f1 = new SimpleData(1, false);

        // encrypt them
        f0.encrypt();
        f1.encrypt();


        // add to the cloud
        cloud.addFile(f0);
        cloud.addFile(f1);

        // get the result of a fetch
        SimpleData result = cloud.get(f0.getDataID());

        System.out.println("Multiplication is: " + result.getValue());

        result.decrypt();



    }
}
