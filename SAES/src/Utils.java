public class Utils {
    // 将16位整数转换为状态矩阵 (4个半字节)
    public static int[] intToState(int value) {
        return new int[]{
            (value >> 12) & 0xF,
            (value >> 8) & 0xF,
            (value >> 4) & 0xF,
            value & 0xF
        };
    }
    
    // 将状态矩阵转换为16位整数
    public static int stateToInt(int[] state) {
        return (state[0] << 12) | (state[1] << 8) | (state[2] << 4) | state[3];
    }
    
    // 行移位
    public static int[] shiftRows(int[] state) {
        return new int[]{state[0], state[1], state[3], state[2]};
    }
    
    // 列混淆
    public static int[] mixColumns(int[] state) {
        int s00 = state[0], s01 = state[1];
        int s10 = state[2], s11 = state[3];
        
        return new int[]{
            s00 ^ GF16Arithmetic.multiply(4, s10),
            s01 ^ GF16Arithmetic.multiply(4, s11),
            GF16Arithmetic.multiply(4, s00) ^ s10,
            GF16Arithmetic.multiply(4, s01) ^ s11
        };
    }
    
    // 逆列混淆
    public static int[] invMixColumns(int[] state) {
        int s00 = state[0], s01 = state[1];
        int s10 = state[2], s11 = state[3];
        
        return new int[]{
            GF16Arithmetic.multiply(9, s00) ^ GF16Arithmetic.multiply(2, s10),
            GF16Arithmetic.multiply(9, s01) ^ GF16Arithmetic.multiply(2, s11),
            GF16Arithmetic.multiply(2, s00) ^ GF16Arithmetic.multiply(9, s10),
            GF16Arithmetic.multiply(2, s01) ^ GF16Arithmetic.multiply(9, s11)
        };
    }
    
    // 轮密钥加
    public static int[] addRoundKey(int[] state, int[] roundKey) {
        return new int[]{
            state[0] ^ roundKey[0],
            state[1] ^ roundKey[1],
            state[2] ^ roundKey[2],
            state[3] ^ roundKey[3]
        };
    }
}