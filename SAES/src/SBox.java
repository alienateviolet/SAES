public class SBox {
    // S盒 (根据PDF表D.1(a))
    private static final int[] S_BOX = {
        0x9, 0x4, 0xA, 0xB,
        0xD, 0x1, 0x8, 0x5,
        0x6, 0x2, 0x0, 0x3,
        0xC, 0xE, 0xF, 0x7
    };
    
    // 逆S盒 (根据PDF表D.1(b))
    private static final int[] INV_S_BOX = {
        0xA, 0x5, 0x9, 0xB,
        0x1, 0x7, 0x8, 0xF,
        0x6, 0x0, 0x2, 0x3,
        0xC, 0x4, 0xD, 0xE
    };
    
    // 半字节代替
    public static int subNibble(int nibble, boolean inverse) {
        int[] box = inverse ? INV_S_BOX : S_BOX;
        return box[nibble & 0xF];
    }
    
    // 对整个状态进行半字节代替
    public static int[] subNibbles(int[] state, boolean inverse) {
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = subNibble(state[i], inverse);
        }
        return result;
    }
}