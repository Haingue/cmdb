---
title: Contributing
description: How to contribute to the CMDB project
---

# Contributing to CMDB

We welcome contributions from the community! This guide will help you get started with contributing to the CMDB project.

## Ways to Contribute

- **Code**: Implement new features, fix bugs, improve performance
- **Documentation**: Improve docs, add examples, fix typos
- **Testing**: Write tests, improve test coverage
- **Bug Reports**: Report issues and suggest fixes
- **Feature Requests**: Suggest new features or improvements
- **Code Review**: Review pull requests from other contributors

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally
3. **Set up the development environment** (see [Getting Started](/getting-started/))
4. **Create a feature branch** for your changes

## Development Setup

### Prerequisites

- Java 17+
- Docker + Docker Compose
- Node.js 18+
- Git

### Setting Up

```bash
# Clone your fork
git clone https://github.com/your-username/cmdb.git
cd cmdb

# Add upstream remote
git remote add upstream https://github.com/Haingue/cmdb.git

# Set up the project (see Getting Started guide)
```

## Creating a Feature Branch

```bash
# Create a new branch
git checkout -b feature/your-feature-name

# Or for bug fixes
git checkout -b fix/your-bug-fix
```

### Branch Naming Convention

| Type | Prefix | Example |
|------|--------|---------|
| Feature | `feature/` | `feature/add-github-integration` |
| Bug Fix | `fix/` | `fix/entity-validation-issue` |
| Documentation | `docs/` | `docs/update-readme` |
| Refactor | `refactor/` | `refactor/service-structure` |
| Chore | `chore/` | `chore/update-dependencies` |

## Coding Standards

### Java

- Follow **Google Java Style Guide** with some exceptions
- Use **4 spaces** for indentation (not tabs)
- Use **UTF-8** encoding
- Maximum line length: **120 characters**
- Use meaningful, descriptive names
- Prefer immutability where possible
- Use `final` for variables that shouldn't change

#### Example

```java
// Good
public class EntityService {
    private final EntityRepository entityRepository;

    public EntityService(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public Entity findById(UUID id) {
        return entityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }
}

// Bad - Avoid
public class E {
    EntityRepository er;

    public E(EntityRepository er) {
        this.er = er;
    }

    public Entity f(UUID i) {
        return er.findById(i).orElse(null);
    }
}
```

### TypeScript/JavaScript

- Use **2 spaces** for indentation
- Use **single quotes** for strings
- Use **semicolons** at the end of statements
- Use **TypeScript** for type safety
- Follow **React** best practices

#### Example

```typescript
// Good
import React, { useState, useEffect } from 'react';

interface EntityProps {
  id: string;
  name: string;
  type: string;
}

const Entity: React.FC<EntityProps> = ({ id, name, type }) => {
  const [details, setDetails] = useState<EntityDetails | null>(null);

  useEffect(() => {
    fetchEntityDetails(id).then(setDetails);
  }, [id]);

  return (
    <div className="entity">
      <h2>{name}</h2>
      <p>Type: {type}</p>
    </div>
  );
};

// Bad - Avoid
const E = ({ i, n, t }) => {
  const [d, s] = useState(null)
  useEffect(() => {
    fetch(i).then(r => s(r))
  }, [i])
  return <div>{n}</div>
}
```

### Markdown

- Use **consistent heading hierarchy** (start with `#` for page titles)
- Use **descriptive link text** (not "click here")
- Use **code blocks** for code examples with proper language specification
- Keep lines at a reasonable length (80-120 characters)

## Testing

### Java Testing

- Use **JUnit 5** for unit and integration tests
- Use **Testcontainers** for integration tests with real databases
- Follow the **Arrange-Act-Assert** pattern

#### Example

```java
class EntityServiceTest {

    @Mock
    private EntityRepository entityRepository;

    @InjectMocks
    private EntityService entityService;

    @Test
    @DisplayName("Should find entity by ID")
    void shouldFindEntityById() {
        // Arrange
        UUID id = UUID.randomUUID();
        Entity expectedEntity = new Entity(id, "Test Entity");
        
        when(entityRepository.findById(id)).thenReturn(Optional.of(expectedEntity));

        // Act
        Entity actualEntity = entityService.findById(id);

        // Assert
        assertThat(actualEntity).isEqualTo(expectedEntity);
        verify(entityRepository).findById(id);
    }
}
```

### Frontend Testing

- Use **Jest** for unit tests
- Use **React Testing Library** for component tests
- Aim for **high test coverage**

## Committing Changes

### Commit Message Format

Follow the **Conventional Commits** specification:

```
type(scope): description

[optional body]

[optional footer]
```

#### Types

| Type | When to Use |
|------|-------------|
| `feat` | A new feature |
| `fix` | A bug fix |
| `docs` | Documentation only changes |
| `style` | Changes that do not affect the meaning of the code (white-space, formatting, etc.) |
| `refactor` | A code change that neither fixes a bug nor adds a feature |
| `perf` | A code change that improves performance |
| `test` | Adding missing tests |
| `chore` | Changes to the build process or auxiliary tools and libraries |

#### Examples

```bash
# Feature
git commit -m "feat(inventory): add entity versioning support"

# Bug fix
git commit -m "fix(inventory): handle null entity name"

# Documentation
git commit -m "docs: update getting started guide"

# Refactor
git commit -m "refactor(core): extract common validation logic"

# Multiple changes
git commit -m "feat(api): add GraphQL endpoint for entities

- Add GraphQL schema for Entity type
- Implement EntityQuery resolver
- Add tests for GraphQL endpoint"
```

### Commit Best Practices

1. **Atomic commits**: Each commit should represent a single logical change
2. **Descriptive messages**: Explain what changed and why
3. **Refer to issues**: Include issue numbers if applicable (e.g., "Fixes #123")
4. **Keep it focused**: Avoid mixing unrelated changes in a single commit

## Submitting Changes

### Before Submitting

1. **Run all tests**:
   ```bash
   # Java tests
   mvn test
   
   # Frontend tests
   cd frontend
   npm test
   ```

2. **Check code formatting**:
   - Ensure your IDE is configured with the project's code style
   - Or use the provided formatting configuration

3. **Build the project**:
   ```bash
   mvn clean install
   cd frontend && npm run build
   ```

4. **Update documentation**:
   - Update relevant documentation for your changes
   - Add ADRs for significant architectural decisions

### Creating a Pull Request

1. **Push your changes**:
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create a Pull Request** on GitHub:
   - Go to the [CMDB repository](https://github.com/Haingue/cmdb)
   - Click "New Pull Request"
   - Select your branch as the compare branch
   - Select the `develop` branch as the base branch

3. **Fill out the PR template**:
   - Provide a clear description of your changes
   - Explain the motivation and context
   - Link to any relevant issues
   - Describe testing done
   - Add any additional context

4. **Request reviews**:
   - Assign reviewers from the project maintainers
   - Be responsive to feedback and make requested changes

### PR Review Process

1. **Initial review**: Maintainers will review your PR within a few days
2. **Feedback**: You may receive comments and requests for changes
3. **Address feedback**: Make the requested changes and push them to your branch
4. **Approval**: Once all feedback is addressed, your PR will be approved
5. **Merge**: A maintainer will merge your PR into the `develop` branch

## Code Review Guidelines

### For Contributors

- **Be responsive**: Address feedback in a timely manner
- **Explain your decisions**: If you disagree with feedback, explain why
- **Keep commits clean**: Squash or rebase commits as requested
- **Test thoroughly**: Ensure all tests pass and new code is tested

### For Reviewers

- **Be constructive**: Provide actionable feedback
- **Focus on quality**: Look for bugs, performance issues, and code quality
- **Be timely**: Review PRs promptly
- **Explain reasoning**: When requesting changes, explain why

## Reporting Issues

When reporting issues, please include:

1. **Description**: Clear description of the issue
2. **Steps to reproduce**: How to trigger the bug
3. **Expected behavior**: What should happen
4. **Actual behavior**: What actually happens
5. **Environment**:
   - Operating system
   - Java version (`java -version`)
   - Node.js version (`node --version`)
   - Docker version (`docker --version`)
6. **Logs**: Relevant log output
7. **Screenshots**: For UI issues

## Code of Conduct

We expect all contributors to follow our [Code of Conduct](https://github.com/Haingue/cmdb/blob/develop/CODE_OF_CONDUCT.md). Be respectful, inclusive, and professional in all interactions.

## Recognition

All contributors will be recognized in the project's `CONTRIBUTORS.md` file (if they wish). Significant contributors may be granted commit access or maintainer status.

## Need Help?

If you have questions about contributing:

- Check the [FAQ](#) (coming soon)
- Open a discussion on [GitHub Discussions](https://github.com/Haingue/cmdb/discussions)
- Join our [community chat](#) (coming soon)

Thank you for contributing to CMDB! 🎉
