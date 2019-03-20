#if UNITY_ANDROID || UNITY_IPHONE || UNITY_STANDALONE_OSX || UNITY_TVOS
// WARNING: Do not modify! Generated file.

namespace UnityEngine.Purchasing.Security {
    public class GooglePlayTangle
    {
        private static byte[] data = System.Convert.FromBase64String("zo5OZWsItJlbOSU6KuiLvmAYdR5Aek0PSSRckqOFgqMIGCa8mIs3XG7LAe5Mim9cGHbMNi/N3LI7kW4VywAo18DYxRt7WQMrF00YY9bXaByLVW4xbbQKdUHvLMxbVhv2Dp6qXgONp+/Lwul4SabaoamoskmjmHlDKfD/xnCI+J88CClgS3UfSfuNiWQPjIKNvQ+Mh48PjIyNLlbNimLseAwxvk1EUZD8QqzYJ0Z2VphItxnBDzy2fK+gbQ6nPmLA3Fe61HBPogfRFvZf9eKhjmKyN7dmQv6DnUiTdRdGB3kwXsZoFKdQXjjI7CZmN0jLUXBI5UB08gjL4XPazQ7mVZg1mMq9D4yvvYCLhKcLxQt6gIyMjIiNjsq7YBizzG7XII+OjI2M");
        private static int[] order = new int[] { 1,13,9,10,9,6,7,12,13,13,11,13,12,13,14 };
        private static int key = 43;

        public static readonly bool IsPopulated = true;

        public static byte[] Data() {
        	if (IsPopulated == false)
        		return null;
            return Obfuscator.DeObfuscate(data, order, key);
        }
    }
}
#endif
