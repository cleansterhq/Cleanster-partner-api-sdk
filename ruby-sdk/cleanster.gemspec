require_relative "lib/cleanster/version"

Gem::Specification.new do |spec|
  spec.name          = "cleanster"
  spec.version       = Cleanster::VERSION
  spec.authors       = ["Cleanster"]
  spec.email         = ["partner@cleanster.com"]
  spec.summary       = "Official Ruby SDK for the Cleanster Partner API"
  spec.description   = "Manage cleaning bookings, properties, users, checklists, payment methods, " \
                       "webhooks, and more through the Cleanster Partner API."
  spec.homepage      = "https://github.com/cleanster/cleanster-ruby-sdk"
  spec.license       = "MIT"

  spec.required_ruby_version = ">= 2.7.0"

  spec.metadata["homepage_uri"]    = spec.homepage
  spec.metadata["source_code_uri"] = "https://github.com/cleanster/cleanster-ruby-sdk"
  spec.metadata["changelog_uri"]   = "https://github.com/cleanster/cleanster-ruby-sdk/blob/main/CHANGELOG.md"

  spec.files = Dir[
    "lib/**/*.rb",
    "README.md",
    "LICENSE",
    "CHANGELOG.md",
    "cleanster.gemspec"
  ]

  spec.require_paths = ["lib"]

  spec.add_development_dependency "rspec", "~> 3.12"
  spec.add_development_dependency "rake",  "~> 13.0"
end
