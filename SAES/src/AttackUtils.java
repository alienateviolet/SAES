import java.util.*;

public class AttackUtils {

    // 中间相遇攻击（简化版，用于演示）
    public static List<Integer> meetInTheMiddleAttack(int plaintext, int ciphertext) {
        System.out.println("开始中间相遇攻击...");
        System.out.println("明文: " + String.format("%04X", plaintext));
        System.out.println("密文: " + String.format("%04X", ciphertext));

        // 为了演示，我们只测试部分密钥空间
        int testRange = 0x10000; // 只测试256个密钥，实际应该是0x10000

        // 存储加密中间结果
        Map<Integer, List<Integer>> encryptMap = new HashMap<>();

        // 第一阶段：用部分可能的K1加密明文
        System.out.println("第一阶段：使用K1加密明文...");
        for (int k1 = 0; k1 < testRange; k1++) {
            int intermediate = SAES.encrypt(plaintext, k1);

            if (!encryptMap.containsKey(intermediate)) {
                encryptMap.put(intermediate, new ArrayList<>());
            }
            encryptMap.get(intermediate).add(k1);

            // 进度显示 - 使用简单的ASCII字符
            if (k1 % 0x20 == 0) {
                int progress = (k1 * 100) / testRange;
                System.out.println("进度: " + progress + "%");
            }
        }

        // 第二阶段：用部分可能的K2解密密文，寻找匹配
        System.out.println("第二阶段：使用K2解密密文...");
        List<Integer> possibleKeys = new ArrayList<>();

        for (int k2 = 0; k2 < testRange; k2++) {
            int intermediate = SAES.decrypt(ciphertext, k2);

            if (encryptMap.containsKey(intermediate)) {
                for (int k1 : encryptMap.get(intermediate)) {
                    int combinedKey = (k1 << 16) | k2;
                    possibleKeys.add(combinedKey);
                }
            }

            // 进度显示 - 使用简单的ASCII字符
            if (k2 % 0x20 == 0) {
                int progress = (k2 * 100) / testRange;
                System.out.println("进度: " + progress + "%");
            }
        }

        System.out.println("找到 " + possibleKeys.size() + " 个可能的密钥对");
        return possibleKeys;
    }

    // 完整版的中间相遇攻击（需要较长时间）
    public static List<Integer> meetInTheMiddleAttackFull(int plaintext, int ciphertext) {
        System.out.println("开始完整中间相遇攻击...");
        System.out.println("警告：这将需要较长时间...");

        Map<Integer, List<Integer>> encryptMap = new HashMap<>();

        // 第一阶段：用所有可能的K1加密明文
        System.out.println("第一阶段：使用所有K1加密明文...");
        for (int k1 = 0; k1 <= 0xFFFF; k1++) {
            int intermediate = SAES.encrypt(plaintext, k1);

            if (!encryptMap.containsKey(intermediate)) {
                encryptMap.put(intermediate, new ArrayList<>());
            }
            encryptMap.get(intermediate).add(k1);

            // 添加进度显示
            if (k1 % 0x1000 == 0) {
                int progress = (k1 * 100) / 0xFFFF;
                System.out.println("第一阶段进度: " + progress + "%");
            }
        }

        // 第二阶段：用所有可能的K2解密密文
        System.out.println("第二阶段：使用所有K2解密密文...");
        List<Integer> possibleKeys = new ArrayList<>();

        for (int k2 = 0; k2 <= 0xFFFF; k2++) {
            int intermediate = SAES.decrypt(ciphertext, k2);

            if (encryptMap.containsKey(intermediate)) {
                for (int k1 : encryptMap.get(intermediate)) {
                    int combinedKey = (k1 << 16) | k2;
                    possibleKeys.add(combinedKey);
                }
            }

            // 添加进度显示
            if (k2 % 0x1000 == 0) {
                int progress = (k2 * 100) / 0xFFFF;
                System.out.println("第二阶段进度: " + progress + "%");
            }
        }

        System.out.println("完整攻击完成，找到 " + possibleKeys.size() + " 个可能的密钥对");
        return possibleKeys;
    }

    // 使用多个明密文对验证密钥
    public static List<Integer> verifyKeysWithMultiplePairs(List<Integer> possibleKeys,
                                                            List<Integer> plaintexts,
                                                            List<Integer> ciphertexts) {
        List<Integer> verifiedKeys = new ArrayList<>();

        System.out.println("开始验证密钥，使用 " + plaintexts.size() + " 对明密文...");

        for (int keyPair : possibleKeys) {
            int k1 = (keyPair >> 16) & 0xFFFF;
            int k2 = keyPair & 0xFFFF;
            boolean valid = true;

            // 验证所有明密文对
            for (int i = 0; i < plaintexts.size(); i++) {
                int encrypted = doubleEncrypt(plaintexts.get(i), k1, k2);
                if (encrypted != ciphertexts.get(i)) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                verifiedKeys.add(keyPair);
            }
        }

        System.out.println("验证完成，剩余 " + verifiedKeys.size() + " 个有效密钥");
        return verifiedKeys;
    }

    public static int doubleEncrypt(int plaintext, int k1, int k2) {
        int temp = SAES.encrypt(plaintext, k1);
        return SAES.encrypt(temp, k2);
    }

    // 新增方法：生成测试用的明密文对
    public static Map<String, Integer> generateTestPair(int k1, int k2, int plaintext) {
        Map<String, Integer> result = new HashMap<>();
        int ciphertext = doubleEncrypt(plaintext, k1, k2);
        result.put("plaintext", plaintext);
        result.put("ciphertext", ciphertext);
        result.put("k1", k1);
        result.put("k2", k2);
        return result;
    }

    // 新增方法：验证单个密钥对
    public static boolean verifyKeyPair(int k1, int k2, List<Integer> plaintexts, List<Integer> ciphertexts) {
        for (int i = 0; i < plaintexts.size(); i++) {
            int encrypted = doubleEncrypt(plaintexts.get(i), k1, k2);
            if (encrypted != ciphertexts.get(i)) {
                return false;
            }
        }
        return true;
    }
}