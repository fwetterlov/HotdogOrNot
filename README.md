# Hot Dog or Not: A Machine Learning Project

## Introduction
This project, inspired by HBO's Silicon Valley, implements a machine learning model to classify images as either hot dogs or not. We utilize TensorFlow and Keras to develop a Convolutional Neural Network (CNN) leveraging the one vs. rest classification strategy.

## Dataset
Data is sourced from Kaggle, comprising about 1800 labeled images each of hot dogs and non-hot dogs. We also used a "control" dataset for validation.

## Preprocessing
Images were resized to 150x150 pixels and pixel values normalized to improve memory efficiency.

## Model Architecture
Our CNN includes Conv2D and MaxPooling2D layers with ReLU activations, and a final Dense layer with a sigmoid function for binary classification.

## Additional Techniques
- **Pre-trained Model**: Experimented with MobileNetV2 for compactness.
- **Early Stopping**: To prevent overfitting.
- **Picture Augmentation**: For rotation and flipping, avoiding misclassification.

## Challenges and Improvements
Focuses on optimizing training time, fine-tuning, and exploring more pre-trained models.

## Conclusion
Achieved 74.53% accuracy on the test set, demonstrating the capabilities and limitations of CNNs in image classification.

