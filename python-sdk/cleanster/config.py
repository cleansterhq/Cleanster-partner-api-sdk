"""
Configuration for the Cleanster SDK client.
"""

SANDBOX_BASE_URL = (
    "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public"
)
PRODUCTION_BASE_URL = (
    "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public"
)


class CleansterConfig:
    """
    Holds all configuration for a CleansterClient instance.

    Use the factory methods or the builder pattern:

        config = CleansterConfig.sandbox("my-key")
        config = CleansterConfig.production("my-key")
        config = CleansterConfig.builder("my-key").timeout(60).build()
    """

    def __init__(
        self,
        access_key: str,
        base_url: str = SANDBOX_BASE_URL,
        timeout: int = 30,
    ):
        if not access_key or not access_key.strip():
            raise ValueError("access_key must not be None or blank.")
        self.access_key = access_key
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout

    @classmethod
    def sandbox(cls, access_key: str) -> "CleansterConfig":
        """Create a config pointing to the sandbox environment."""
        return cls(access_key=access_key, base_url=SANDBOX_BASE_URL)

    @classmethod
    def production(cls, access_key: str) -> "CleansterConfig":
        """Create a config pointing to the production environment."""
        return cls(access_key=access_key, base_url=PRODUCTION_BASE_URL)

    @classmethod
    def builder(cls, access_key: str) -> "CleansterConfigBuilder":
        """Return a builder for custom configuration."""
        return CleansterConfigBuilder(access_key)


class CleansterConfigBuilder:
    """Fluent builder for CleansterConfig."""

    def __init__(self, access_key: str):
        self._access_key = access_key
        self._base_url = SANDBOX_BASE_URL
        self._timeout = 30

    def base_url(self, url: str) -> "CleansterConfigBuilder":
        self._base_url = url
        return self

    def sandbox(self) -> "CleansterConfigBuilder":
        self._base_url = SANDBOX_BASE_URL
        return self

    def production(self) -> "CleansterConfigBuilder":
        self._base_url = PRODUCTION_BASE_URL
        return self

    def timeout(self, seconds: int) -> "CleansterConfigBuilder":
        self._timeout = seconds
        return self

    def build(self) -> CleansterConfig:
        return CleansterConfig(
            access_key=self._access_key,
            base_url=self._base_url,
            timeout=self._timeout,
        )
