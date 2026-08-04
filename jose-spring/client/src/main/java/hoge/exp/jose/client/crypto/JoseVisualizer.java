package hoge.exp.jose.client.crypto;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;

public class JoseVisualizer {

    public static void printJoseFlow(String stepName, String plainJson, String jwsCompact, String jweCompact) {
        try {
            System.out.println("\n================================================================================");
            System.out.println(" [JOSE CRYPTO FLOW] " + stepName);
            System.out.println("================================================================================");

            // 1. Plaintext JSON
            System.out.println("1. [Plaintext JSON Payload]");
            System.out.println("   " + plainJson);

            // 2. JWS
            JWSObject jwsObject = JWSObject.parse(jwsCompact);
            System.out.println("\n2. [JWS Signature (EdDSA with Ed25519)]");
            System.out.println("   ・JWS Header  : " + jwsObject.getHeader().toJSONObject());
            System.out.println("   ・JWS Payload : " + jwsObject.getPayload().toJSONObject());
            System.out.println("   ・JWS String (3 Parts: Header.Payload.Signature):");
            System.out.println("     " + jwsCompact);

            // 3. JWE
            JWEObject jweObject = JWEObject.parse(jweCompact);
            System.out.println("\n3. [JWE Encryption (ECDH-ES+A256KW + A256GCM)]");
            System.out.println("   ・JWE Header (includes Ephemeral Public Key 'epk'):");
            System.out.println("     " + jweObject.getHeader().toJSONObject());

            String[] parts = jweCompact.split("\\.", -1);
            System.out.println("   ・JWE 5 Parts Breakdown:");
            System.out.println("     [Part 1: Header]       " + truncate(parts[0], 45));
            System.out.println("     [Part 2: EncryptedKey] " + truncate(parts[1], 45));
            System.out.println("     [Part 3: IV]           " + truncate(parts[2], 45));
            System.out.println("     [Part 4: Ciphertext]   " + truncate(parts[3], 45));
            System.out.println("     [Part 5: Auth Tag]     " + truncate(parts[4], 45));

            System.out.println("\n   ・HTTP Request Body (Raw JWE Compact String):");
            System.out.println("     " + jweCompact);
            System.out.println("================================================================ algorithm\n");
        } catch (Exception e) {
            System.err.println("[WARN] Failed to visualize JOSE flow: " + e.getMessage());
        }
    }

    public static void printJoseResponseFlow(String stepName, String rawJweResponseCompact, String jwsCompact, String plainResponseJson, boolean isSignatureValid) {
        try {
            System.out.println("\n================================================================================");
            System.out.println(" [JOSE CRYPTO FLOW] " + stepName);
            System.out.println("================================================================================");

            // 1. Raw HTTP Response Body
            System.out.println("1. [Received HTTP Response Body (Raw JWE Compact String)]");
            System.out.println("   " + rawJweResponseCompact);

            // 2. JWE Breakdown
            JWEObject jweObject = JWEObject.parse(rawJweResponseCompact);
            System.out.println("\n2. [JWE Decryption Breakdown (Server -> Client)]");
            System.out.println("   ・JWE Header : " + jweObject.getHeader().toJSONObject());
            String[] parts = rawJweResponseCompact.split("\\.", -1);
            System.out.println("   ・JWE 5 Parts:");
            System.out.println("     [Part 1: Header]       " + truncate(parts[0], 45));
            System.out.println("     [Part 2: EncryptedKey] " + truncate(parts[1], 45));
            System.out.println("     [Part 3: IV]           " + truncate(parts[2], 45));
            System.out.println("     [Part 4: Ciphertext]   " + truncate(parts[3], 45));
            System.out.println("     [Part 5: Auth Tag]     " + truncate(parts[4], 45));

            // 3. JWS Signature Verification
            JWSObject jwsObject = JWSObject.parse(jwsCompact);
            System.out.println("\n3. [JWS Signature Verification (Server Signature)]");
            System.out.println("   ・Server JWS Header  : " + jwsObject.getHeader().toJSONObject());
            System.out.println("   ・Server JWS Payload : " + jwsObject.getPayload().toJSONObject());
            System.out.println("   ・Server JWS String (3 Parts: Header.Payload.Signature):");
            System.out.println("     " + jwsCompact);
            System.out.println("   ・JWS Verification Result: " + (isSignatureValid ? "SUCCESS (Signed by Server)" : "FAILED"));

            // 4. Decrypted Plaintext Payload
            System.out.println("\n4. [Decrypted Response Plaintext JSON]");
            System.out.println("   " + plainResponseJson);
            System.out.println("================================================================================\n");
        } catch (Exception e) {
            System.err.println("[WARN] Failed to visualize JOSE response flow: " + e.getMessage());
        }
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "... (len=" + text.length() + ")";
    }
}
