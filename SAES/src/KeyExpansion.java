public class KeyExpansion {
    // 轮常数
    private static final int[] RCON = {0x80, 0x30};
    
    public static int[][] expandKey(int key) {
        int[][] roundKeys = new int[3][];
        
        // 将16位密钥分成两个8位字
        int w0 = (key >> 8) & 0xFF;
        int w1 = key & 0xFF;
        
        // 计算w2 = w0 ⊕ g(w1)
        int w2 = w0 ^ gFunction(w1, 0);
        
        // 计算w3 = w2 ⊕ w1
        int w3 = w2 ^ w1;
        
        // 计算w4 = w2 ⊕ g(w3)
        int w4 = w2 ^ gFunction(w3, 1);
        
        // 计算w5 = w4 ⊕ w3
        int w5 = w4 ^ w3;
        
        // 构建轮密钥
        roundKeys[0] = new int[]{(w0 >> 4) & 0xF, w0 & 0xF, (w1 >> 4) & 0xF, w1 & 0xF};
        roundKeys[1] = new int[]{(w2 >> 4) & 0xF, w2 & 0xF, (w3 >> 4) & 0xF, w3 & 0xF};
        roundKeys[2] = new int[]{(w4 >> 4) & 0xF, w4 & 0xF, (w5 >> 4) & 0xF, w5 & 0xF};
        
        return roundKeys;
    }
    
    private static int gFunction(int word, int round) {
        // 循环左移4位 (RotNib)
        int rotated = ((word << 4) | (word >> 4)) & 0xFF;
        
        // 半字节代替 (SubNib)
        int subNibbled = (SBox.subNibble((rotated >> 4) & 0xF, false) << 4) |
                         SBox.subNibble(rotated & 0xF, false);
        
        // 与轮常数异或
        return subNibbled ^ RCON[round];
    }
}