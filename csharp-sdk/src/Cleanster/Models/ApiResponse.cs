namespace Cleanster.Models;

/// <summary>
/// Standard response wrapper returned by every SDK method.
/// </summary>
/// <typeparam name="T">The type of the <see cref="Data"/> payload.</typeparam>
/// <param name="Status">HTTP-style status code (e.g., 200).</param>
/// <param name="Message">Human-readable status string (e.g., "OK").</param>
/// <param name="Data">Typed payload — a model object, list of models, or raw JsonElement.</param>
public sealed record ApiResponse<T>(int Status, string Message, T Data);
