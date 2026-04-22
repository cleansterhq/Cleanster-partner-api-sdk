from setuptools import setup, find_packages

with open("README.md", encoding="utf-8") as f:
    long_description = f.read()

setup(
    name="cleanster",
    version="1.0.0",
    author="Cleanster",
    author_email="partner@cleanster.com",
    description="Official Python SDK for the Cleanster Partner API",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/cleanster/cleanster-python-sdk",
    project_urls={
        "Bug Tracker": "https://github.com/cleanster/cleanster-python-sdk/issues",
        "API Documentation": "https://documenter.getpostman.com/view/26172658/2sAYdoF7ep",
    },
    packages=find_packages(exclude=["tests*"]),
    python_requires=">=3.8",
    install_requires=[
        "requests>=2.28.0",
    ],
    extras_require={
        "dev": [
            "pytest>=7.0",
            "pytest-cov>=4.0",
        ],
    },
    classifiers=[
        "Development Status :: 5 - Production/Stable",
        "Intended Audience :: Developers",
        "License :: OSI Approved :: MIT License",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: 3.11",
        "Programming Language :: Python :: 3.12",
        "Topic :: Software Development :: Libraries :: Python Modules",
        "Operating System :: OS Independent",
    ],
    keywords="cleanster cleaning api sdk partner booking",
)
