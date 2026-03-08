package chatbot;

/**
 * Represents exceptions specific to BryanChatbot.
 */
class ChatbotException extends Exception {

    /**
     * Constructs a chatbot exception with the given message.
     *
     * @param message: Error message describing the exception.
     */
    public ChatbotException(String message) {
        super(message);
    }
}