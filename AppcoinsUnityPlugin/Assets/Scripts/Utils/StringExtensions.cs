using System;

public static class StringExtensions
{
    public static bool Contains(this string source, string toCheck, StringComparison comp)
    {
        if (source == null) return false;
        return source.IndexOf(toCheck, comp) >= 0;
    }
}