class KeychainManager {
    static let shared = KeychainManager()

    private let serviceName = "com.example.AIChat"

    func save(key: String, value: String) -> Bool {
        guard let data = value.data(using: .utf8) else { return false }

        let query: [CFString: Any] = [
            kSecClass: kSecClassGenericPassword,
            kSecAttrService: serviceName,
            kSecAttrAccount: key,
            kSecValueData: data
        ]

        SecItemDelete(query as CFDictionary)
        return SecItemAdd(query as CFDictionary, nil) == errSecSuccess
    }

    func load(key: String) -> String? {
        let query: [CFString: Any] = [
            kSecClass: kSecClassGenericPassword,
            kSecAttrService: serviceName,
            kSecAttrAccount: key,
            kSecReturnData: kCFBooleanTrue!,
            kSecMatchLimit: kSecMatchLimitOne
        ]

        var dataTypeRef: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &dataTypeRef)

        if status == errSecSuccess, let data = dataTypeRef as? Data {
            return String(data: data, encoding: .utf8)
        }
        return nil
    }
}

// Swift 与 KMM 交互
class IOSPlatformSettings: PlatformSettings {
    override func getApiKey(provider: ModelProvider, type: KeyType) -> String {
        let key = switch type {
        case .apiKey: "\(provider.name)_API_KEY"
        case .clientId: "\(provider.name)_CLIENT_ID"
        case .secret: "\(provider.name)_SECRET"
        default: ""
        }
        return KeychainManager.shared.load(key: key) ?? ""
    }

    override func setApiKey(provider: ModelProvider, type: KeyType, value: String) {
        let key = switch type {
        case .apiKey: "\(provider.name)_API_KEY"
        case .clientId: "\(provider.name)_CLIENT_ID"
        case .secret: "\(provider.name)_SECRET"
        default: ""
        }
        KeychainManager.shared.save(key: key, value: value)
    }
}