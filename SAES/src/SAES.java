public class SAES {
    
    public static int encrypt(int plaintext, int key) {
        int[][] roundKeys = KeyExpansion.expandKey(key);
        int[] state = Utils.intToState(plaintext);
        
        // 第0轮: 轮密钥加
        state = Utils.addRoundKey(state, roundKeys[0]);
        
        // 第1轮: 完整轮
        state = SBox.subNibbles(state, false);  // 半字节代替
        state = Utils.shiftRows(state);         // 行移位
        state = Utils.mixColumns(state);        // 列混淆
        state = Utils.addRoundKey(state, roundKeys[1]); // 轮密钥加
        
        // 第2轮: 简化轮
        state = SBox.subNibbles(state, false);  // 半字节代替
        state = Utils.shiftRows(state);         // 行移位
        state = Utils.addRoundKey(state, roundKeys[2]); // 轮密钥加
        
        return Utils.stateToInt(state);
    }
    
    public static int decrypt(int ciphertext, int key) {
        int[][] roundKeys = KeyExpansion.expandKey(key);
        int[] state = Utils.intToState(ciphertext);
        
        // 第2轮逆
        state = Utils.addRoundKey(state, roundKeys[2]);
        state = Utils.shiftRows(state);         // 逆行移位
        state = SBox.subNibbles(state, true);   // 逆半字节代替
        
        // 第1轮逆
        state = Utils.addRoundKey(state, roundKeys[1]);
        state = Utils.invMixColumns(state);     // 逆列混淆
        state = Utils.shiftRows(state);         // 逆行移位
        state = SBox.subNibbles(state, true);   // 逆半字节代替
        
        // 第0轮逆
        state = Utils.addRoundKey(state, roundKeys[0]);
        
        return Utils.stateToInt(state);
    }
}