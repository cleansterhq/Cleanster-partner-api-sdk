namespace Cleanster.Exceptions;

/// <summary>
/// Base exception for all Cleanster SDK errors.
///
/// Thrown for network failures, HTTP timeouts, and JSON parse errors.
/// Catch this class to handle any Cleanster-related error in one place.
/// </summary>
public class CleansterException : Exception
{
    /// <inheritdoc/>
    public CleansterException(string message) : base(message) { }

    /// <inheritdoc/>
    public CleansterException(string message, Exception innerException)
        : base(message, innerException) { }
}
