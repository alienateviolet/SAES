import java.util.Scanner;

public class SAESInteractor {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("===== SAES加解密工具 =====");
            System.out.println("1. 加密");
            System.out.println("2. 解密");
            System.out.println("3. 退出");
            System.out.print("请选择操作(1-3): ");
            
            int choice = scanner.nextInt();
            if (choice == 3) {
                System.out.println("程序已退出");
                break;
            }
            
            System.out.print("请输入16位十六进制密钥(0000-FFFF): ");
            String keyStr = scanner.next().trim();
            int key;
            try {
                key = Integer.parseInt(keyStr, 16);
                // 验证密钥是否为16位(4个半字节)
                if (key < 0 || key > 0xFFFF) {
                    System.out.println("密钥必须是16位十六进制数(0000-FFFF)");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("无效的十六进制格式");
                continue;
            }
            
            System.out.print("请输入16位十六进制(0000-FFFF)" + (choice == 1 ? "明文" : "密文") + ": ");
            String dataStr = scanner.next().trim();
            int data;
            try {
                data = Integer.parseInt(dataStr, 16);
                // 验证数据是否为16位
                if (data < 0 || data > 0xFFFF) {
                    System.out.println("数据必须是16位十六进制数(0000-FFFF)");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("无效的十六进制格式");
                continue;
            }
            
            int result;
            if (choice == 1) {
                result = SAES.encrypt(data, key);
                System.out.println("加密结果: " + String.format("%04X", result));
            } else {
                result = SAES.decrypt(data, key);
                System.out.println("解密结果: " + String.format("%04X", result));;
            }
            System.out.println("-------------------------\n");
        }
        
        scanner.close();
    }
}