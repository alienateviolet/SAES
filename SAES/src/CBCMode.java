import java.util.*;

public class CBCMode {

    // 生成随机初始向量
    public static int generateIV() {
        Random random = new Random();
        return random.nextInt(0x10000);
    }

    // CBC模式加密
    public static List<Integer> encryptCBC(List<Integer> plaintextBlocks, int key, int iv) {
        List<Integer> ciphertextBlocks = new ArrayList<>();
        int previousBlock = iv;

        for (int block : plaintextBlocks) {
            // 与上一块密文异或
            int xored = block ^ previousBlock;
            // 加密
            int encrypted = SAES.encrypt(xored, key);
            ciphertextBlocks.add(encrypted);
            previousBlock = encrypted;
        }

        return ciphertextBlocks;
    }

    // CBC模式解密
    public static List<Integer> decryptCBC(List<Integer> ciphertextBlocks, int key, int iv) {
        List<Integer> plaintextBlocks = new ArrayList<>();
        int previousBlock = iv;

        for (int block : ciphertextBlocks) {
            // 解密
            int decrypted = SAES.decrypt(block, key);
            // 与上一块密文异或
            int xored = decrypted ^ previousBlock;
            plaintextBlocks.add(xored);
            previousBlock = block;
        }

        return plaintextBlocks;
    }

    // 将字符串分割为16位块
    public static List<Integer> stringToBlocks(String text) {
        List<Integer> blocks = new ArrayList<>();

        // 填充到偶数长度
        if (text.length() % 2 != 0) {
            text += " ";
        }

        for (int i = 0; i < text.length(); i += 2) {
            char c1 = text.charAt(i);
            char c2 = text.charAt(i + 1);
            int block = (c1 << 8) | (c2 & 0xFF);
            blocks.add(block);
        }

        return blocks;
    }

    // 将块转换为字符串
    public static String blocksToString(List<Integer> blocks) {
        StringBuilder sb = new StringBuilder();

        for (int block : blocks) {
            char c1 = (char) ((block >> 8) & 0xFF);
            char c2 = (char) (block & 0xFF);
            if (c1 != 0) sb.append(c1);
            if (c2 != 0) sb.append(c2);
        }

        return sb.toString().trim();
    }

    // 显示块内容（用于调试）
    public static void displayBlocks(String label, List<Integer> blocks) {
        System.out.print(label + ": ");
        for (int block : blocks) {
            System.out.print(String.format("%04X ", block));
        }
        System.out.println();
    }
}