package cleanster

import (
	"fmt"
	"strings"
	"time"
)

const (
	// SandboxBaseURL is the base URL for the Cleanster sandbox environment.
	// Use this for development and testing — no real charges or cleaners.
	SandboxBaseURL = "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public"

	// ProductionBaseURL is the base URL for the Cleanster production environment.
	// Use this for live traffic — real cleaners will be dispatched and charges applied.
	ProductionBaseURL = "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public"

	// DefaultTimeout is the default HTTP request timeout.
	DefaultTimeout = 30 * time.Second
)

// Config holds all configuration for a Client instance.
type Config struct {
	// AccessKey is your partner access key sent as the "access-key" header.
	AccessKey string

	// BaseURL is the API base URL. Use SandboxBaseURL or ProductionBaseURL,
	// or supply a custom URL for proxying.
	BaseURL string

	// Timeout is the maximum duration for an individual HTTP request.
	// Defaults to 30 seconds if zero.
	Timeout time.Duration
}

func (c Config) validate() error {
	if strings.TrimSpace(c.AccessKey) == "" {
		return fmt.Errorf("cleanster: Config.AccessKey must not be empty")
	}
	if c.BaseURL == "" {
		return fmt.Errorf("cleanster: Config.BaseURL must not be empty")
	}
	return nil
}

func (c Config) withDefaults() Config {
	if c.Timeout == 0 {
		c.Timeout = DefaultTimeout
	}
	c.BaseURL = strings.TrimRight(c.BaseURL, "/")
	return c
}

// NewSandboxConfig returns a Config pre-configured for the sandbox environment.
func NewSandboxConfig(accessKey string) Config {
	return Config{
		AccessKey: accessKey,
		BaseURL:   SandboxBaseURL,
		Timeout:   DefaultTimeout,
	}
}

// NewProductionConfig returns a Config pre-configured for the production environment.
func NewProductionConfig(accessKey string) Config {
	return Config{
		AccessKey: accessKey,
		BaseURL:   ProductionBaseURL,
		Timeout:   DefaultTimeout,
	}
}
