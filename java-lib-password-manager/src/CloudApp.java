public class CloudApp {

    public static void main(String[] args) {

        String token = null;
        String remotePath = null;
        String localPath = null;

        if(args.length == 4) {

            token = args[1];
            remotePath = args[2];
            localPath = args[3];

            switch(args[0]) {
                case "load" -> CloudLib.load(token, remotePath, localPath);
                case "upload" -> CloudLib.upload(token, remotePath, localPath);
                default -> System.out.println("no command");
            }
        }
    }
}