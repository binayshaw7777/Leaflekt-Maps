import SwiftUI

enum AppDestination {
    case launcher
    case demo
    case sample
}

struct LauncherView: View {
    @State private var destination: AppDestination = .launcher

    var body: some View {
        switch destination {
        case .launcher:
            launcherScreen
        case .demo:
            ContentView(onBack: { destination = .launcher })
        case .sample:
            SampleView(onBack: { destination = .launcher })
        }
    }

    private var launcherScreen: some View {
        VStack(spacing: 0) {
            Spacer()

            VStack(spacing: 8) {
                Text("LeafleKT")
                    .font(.system(size: 48, weight: .bold, design: .rounded))
                    .foregroundStyle(Color.accentColor)

                Text("Compose-first Leaflet — Android + iOS")
                    .font(.body)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
            }

            Spacer().frame(height: 48)

            VStack(spacing: 16) {
                Button {
                    destination = .demo
                } label: {
                    Text("Launch Demo App")
                        .font(.body.weight(.semibold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.large)

                Button {
                    destination = .sample
                } label: {
                    Text("Launch Sample App")
                        .font(.body.weight(.semibold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                }
                .buttonStyle(.bordered)
                .controlSize(.large)
            }

            Spacer().frame(height: 32)

            Text("Demo App shows core library features. Sample App shows advanced integrations like Ola Maps.")
                .font(.caption)
                .foregroundStyle(.tertiary)
                .multilineTextAlignment(.center)

            Spacer()
        }
        .padding(.horizontal, 24)
    }
}
